package it.polimi.sr.wasp.vocals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.sr.wasp.server.enums.HttpMethod;
import it.polimi.sr.wasp.server.model.Endpoint;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.Param;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;
import it.polimi.sr.wasp.vocals.annotations.services.Catalog;
import it.polimi.sr.wasp.vocals.annotations.services.ProcessingService;
import it.polimi.sr.wasp.vocals.annotations.services.PublishingService;
import it.polimi.sr.wasp.vocals.vocabs.VOCALS;
import it.polimi.sr.wasp.vocals.vocabs.VSD;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import spark.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.*;

@Log
public class VocalsFactoryJena {

    public static final Map<String, String> prefixMap = new HashMap<>();

    static {
        prefixMap.put("vocals", VOCALS.getUri());
        prefixMap.put("vsd", VSD.getUri());
        prefixMap.put("xsd", XSD.getURI());
        prefixMap.put("frmt", "http://www.w3.org/ns/formats/");
    }


    public static Model toVocals(Class o, String name, String base) {
        return toVocals(o);
    }

    public static Model toVocals(Object o, String name, String base) {
        return toVocals(o.getClass());
    }

    public static Model toVocals(final Class<?> engine) {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(prefixMap);
        Resource e = getEngineResource(engine, model);

        String engine_base = e.getNameSpace();

        Random random = new Random(0);

        Class<?>[] interfaces = engine.getInterfaces();
        Arrays.stream(interfaces)
                //.filter(i -> i.isAnnotationPresent(Feature.class))
                .forEach((Class<?> clazz) -> Arrays.stream(clazz.getMethods())
                        .forEachOrdered(method -> {

//                    Class<?> returnType = method.getReturnType();
//                    if (returnType.isAnnotationPresent(Exposed.class)) {
//                        Exposed annotation = returnType.getAnnotation(Exposed.class);
//                        String name = annotation.name();
//                        String endpt = URIUtils.SLASH + name;
//                        createGetterEndpoint(graph, e, name, endpt, "");
//                        Arrays.stream(returnType.getFields())
//                                .filter(field -> field.isAnnotationPresent(Key.class))
//                                .map(Field::getName).forEach(field ->
//                                createGetterEndpoint(graph, e, name, URIUtils.addParam(endpt, field), field));
//                    }
//
//                    if (returnType.isAnnotationPresent(Deletable.class)) {
//                        Deletable del = returnType.getAnnotation(Deletable.class);
//                        String delname = del.name();
//                        String endpt = URIUtils.SLASH + delname;
//                        createDeleteEndpoint(graph, e, delname, endpt, "");
//                        Arrays.stream(returnType.getFields())
//                                .filter(field -> field.isAnnotationPresent(Key.class))
//                                .map(Field::getName).forEach(field ->
//                                createDeleteEndpoint(graph, e, delname, URIUtils.addParam(endpt, field), field));
//                    }

                            Resource service = model.createResource();
                            e.addProperty(VSD.hasService, service);

                            Feature feature_annotation =
                                    clazz.isAnnotationPresent(Feature.class) ?
                                            clazz.getAnnotation(Feature.class) :
                                            method.getAnnotation(Feature.class);

                            RSPService annotation1 = method.getAnnotation(RSPService.class);
                            String endpoint = annotation1.endpoint();

                            for (Parameter p : method.getParameters()) {
                                Param param = p.getAnnotation(Param.class);
                                Resource pbn = model.createResource();

                                if (param.uri()) {
                                    endpoint += "/:" + param.name();
                                    service.addProperty(VSD.uri_param, pbn);
                                    pbn.addProperty(VSD.type, type_selector(p.getType()));
                                    pbn.addProperty(VSD.index, p.getName().replace("arg", ""));
                                } else {
                                    JsonObject serialize = serialize(param, p.getType()).getAsJsonObject();
                                    serialize.entrySet().forEach(entry -> {
                                        Resource bp = model.createResource();
                                        service.addProperty(VSD.body_param, bp);
                                        bp.addProperty(VSD.name, entry.getKey());
                                        Resource t = !entry.getValue().isJsonArray() ?
                                                model.createResource(entry.getValue().getAsString()) :
                                                model.createResource("array");

                                        bp.addProperty(org.apache.jena.vocabulary.RDF.type, t)
                                                .addProperty(VSD.index, p.getName().replace("arg", ""));
                                    });

                                    service.addProperty(VSD.body, serialize.toString());
                                }

                                pbn.addProperty(VSD.name, param.name());
                            }

                            String ns = "UNKNOWN".equals(feature_annotation.ns()) ? engine_base + "/" : feature_annotation.ns();

                            Resource feat = model.createProperty(ns, feature_annotation.name());
                            service.addProperty(VOCALS.feature, feat);
                            service.addProperty(VSD.name, feature_annotation.name());
                            service.addProperty(VSD.endpoint, endpoint);
                            service.addProperty(VSD.method, annotation1.method().name());
                        }));

        return model;
    }

    private static void createDeleteEndpoint(Model m, Resource e, String name, String endpt, String key) {
        Resource rr = m.createResource("d" + endpt);
        e.addProperty(VSD.hasService, rr);
        rr.addProperty(VSD.name, "d" + endpt)
                .addProperty(VSD.endpoint, endpt)
                .addProperty(VSD.method, HttpMethod.DELETE.name())
                .addProperty(VOCALS.feature, VSD.ModelDeletion);

        if (key != null && !key.isEmpty()) {
            Resource p = m.createResource();
            rr.addProperty(VSD.uri_param, p);
            p.addProperty(VSD.name, key).addProperty(VSD.index, "0");
        }
    }

    private static void createGetterEndpoint(Model m, Resource e, String name, String endpt, String key) {
        Resource rr = m.createResource(endpt);
        e.addProperty(VSD.hasService, rr);
        rr.addProperty(VSD.name, endpt)
                .addProperty(VSD.endpoint, endpt)
                .addProperty(VSD.method, HttpMethod.GET.name())
                .addProperty(VOCALS.feature, VSD.ModelExposure);
        if (key != null && !key.isEmpty()) {
            Resource p = m.createResource();
            rr.addProperty(VSD.uri_param, p);
            p.addProperty(VSD.name, key).addProperty(VSD.index, "0");
        }
    }


    private static Resource getEngineResource(Class<?> engine, Model model) {
        String uri = "";
        if (engine.isAnnotationPresent(PublishingService.class)) {
            PublishingService cat = engine.getAnnotation(PublishingService.class);
            uri = "http://" + cat.host() + ":" + cat.port();
            return model.createResource(StringUtils.removeLeadingAndTrailingSlashesFrom(uri))
                    .addProperty(org.apache.jena.vocabulary.RDF.type, VSD.PublishingService)
                    .addProperty(VSD.base, uri);

        } else if (engine.isAnnotationPresent(ProcessingService.class)) {
            ProcessingService cat = engine.getAnnotation(ProcessingService.class);
            uri = "http://" + cat.host() + ":" + cat.port();
            return model.createResource(StringUtils.removeLeadingAndTrailingSlashesFrom(uri))
                    .addProperty(org.apache.jena.vocabulary.RDF.type, VSD.ProcessingService)
                    .addProperty(VSD.base, uri);
        }
        if (engine.isAnnotationPresent(Catalog.class)) {
            Catalog cat = engine.getAnnotation(Catalog.class);
            uri = "http://" + cat.host() + ":" + cat.port();
            return model.createResource(StringUtils.removeLeadingAndTrailingSlashesFrom(uri))
                    .addProperty(org.apache.jena.vocabulary.RDF.type, VSD.CatalogService)
                    .addProperty(VSD.base, uri);
        }

        return model.createResource();
    }

    public static JsonElement serialize(Param param, Class<?> c) {
        return serialize(param, new JsonObject(), "", c);
    }

    public static JsonElement serialize(Param param2, JsonElement obj, String name, Class<?> c) {
        if (c.isPrimitive() || String.class.equals(c)) {
            String n = name.isEmpty() ? param2.name() : name;
            if (obj.isJsonObject())
                ((JsonObject) obj).addProperty(n, type_selector(c).getURI());
        } else if (c.isArray()) {
            if (obj.isJsonObject()) {
                ((JsonObject) obj).add(name, new JsonArray());
            }
        } else {
            Arrays.stream(c.getFields())
                    .forEach(field -> serialize(param2, obj, field.getName(), field.getType()));

            Arrays.stream(c.getMethods())
                    .filter(m -> m.getName().
                            startsWith("set"))
                    .filter(method -> method.getParameterCount() == 1)
                    .forEach(method ->
                            serialize(param2, obj, method.getName().replace("set", ""), method.getParameters()[0].getType()));
        }

        return obj;
    }

    public static Resource type_selector(Class<?> c) {
        if (String.class.equals(c))
            return XSD.xstring;
        else if (Integer.class.equals(c))
            return XSD.integer;
        else if (Long.class.equals(c))
            return XSD.xlong;
        else if (Boolean.class.equals(c))
            return XSD.xboolean;
        else if (Float.class.equals(c))
            return XSD.xfloat;
        else if (Double.class.equals(c))
            return XSD.xdouble;
        else return XSD.xstring;
    }

    public static List<Endpoint> fromVocals(Model model) {

        try {
            Set<Endpoint> set = new HashSet<>();

            String qstring = IOUtils.toString(VocalsFactoryJena.class.getClassLoader().getResourceAsStream("endpoints.sparql"), Charset.defaultCharset());
            String uri_query = IOUtils.toString(VocalsFactoryJena.class.getClassLoader().getResourceAsStream("uri_params.sparql"), Charset.defaultCharset());
            String body_query = IOUtils.toString(VocalsFactoryJena.class.getClassLoader().getResourceAsStream("body.sparql"), Charset.defaultCharset());

            Query q = QueryFactory.create(qstring);
            ParameterizedSparqlString parametrized_uri_query = new ParameterizedSparqlString();
            parametrized_uri_query.setCommandText(uri_query);

            ParameterizedSparqlString parametrized_body_query = new ParameterizedSparqlString();
            parametrized_body_query.setCommandText(body_query);

            QueryExecution queryExecution = QueryExecutionFactory.create(q, model);

            ResultSet res = queryExecution.execSelect();

            while (res.hasNext()) {

                List<Endpoint.Par> params = new ArrayList<>();
                QuerySolution s = res.next();

                parametrized_uri_query.setParam("?feature", s.get("?feature"));
                parametrized_body_query.setParam("?feature", s.get("?feature"));

                log.info(parametrized_uri_query.toString());

                Query query = parametrized_uri_query.asQuery();
                ResultSet param = QueryExecutionFactory.create(query, model).execSelect();

                while (param.hasNext()) {
                    QuerySolution next = param.next();
                    String name = next.get("?name").toString();
                    int index = Integer.parseInt(next.get("?index").toString());
                    params.add(new Endpoint.Par(name, index, true));
                }

                query = parametrized_body_query.asQuery();
                param = QueryExecutionFactory.create(query, model).execSelect();
                log.info(parametrized_body_query.toString());

                while (param.hasNext()) {
                    QuerySolution next = param.next();
                    String name = next.get("?name").toString().replace("\\", "");
                    int index = Integer.parseInt(next.get("?index").toString());
                    log.info(name);
                    params.add(new Endpoint.Par(name, index, false));
                }

                params.sort((o1, o2) -> o1.index < o2.index ? -1 : (
                        o1.index == o2.index ? 0 : -1));

                String feature = "";
                if (s.contains("?feature")) {
                    feature = s.get("?feature").toString();
                } else if (s.contains("?method") && "GET".equals(s.get("?method").toString())) {
                    feature = params.size() > 1 ? ":GetterFeatureN" : ":GetterFeature" + params.size();
                }

                Endpoint.Par[] params1 = new Endpoint.Par[params.size()];

                for (int i = 0; i < params.size(); i++) {
                    params1[i] = params.get(i);
                }

                set.add(new Endpoint(
                        s.get("?name").toString(),
                        s.get("?endpoint").toString(),
                        HttpMethod.valueOf(s.get("?method").toString()),
                        feature,
                        params1));
            }

            return new ArrayList<>(set);
        } catch (IOException e) {
            return new ArrayList<>();
        }

    }


}
