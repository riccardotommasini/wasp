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
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import spark.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.*;

@Log
public class VocalsFactoryRDF4J {

    public static final Map<String, String> prefixMap = new HashMap<>();
    static final ValueFactory vf = SimpleValueFactory.getInstance();

    static {
        prefixMap.put("vocals", VOCALS.getUri());
        prefixMap.put("vsd", VSD.getUri());
        prefixMap.put("xsd", XMLSchema.NAMESPACE);
        prefixMap.put("frmt", "http://www.w3.org/ns/formats/");
    }

    public static org.eclipse.rdf4j.model.Model toVocals(final Class<?> engine) {
        Model model = ModelFactory.createDefaultModel();
        ModelBuilder builder = new ModelBuilder();

        prefixMap.entrySet().stream().forEach(e -> builder.setNamespace(e.getKey(), e.getValue()));

        IRI hasService = vf.createIRI(VSD.hasService.getNameSpace(), VSD.hasService.getLocalName());
        IRI uri_param = vf.createIRI(VSD.uri_param.getNameSpace(), VSD.uri_param.getLocalName());
        IRI body_param = vf.createIRI(VSD.body_param.getNameSpace(), VSD.body_param.getLocalName());
        IRI type = vf.createIRI(VSD.type.getNameSpace(), VSD.type.getLocalName());
        IRI index = vf.createIRI(VSD.index.getNameSpace(), VSD.index.getLocalName());
        IRI name = vf.createIRI(VSD.name.getNameSpace(), VSD.name.getLocalName());
        IRI body = vf.createIRI(VSD.body.getNameSpace(), VSD.body.getLocalName());
        IRI endpoint = vf.createIRI(VSD.endpoint.getNameSpace(), VSD.endpoint.getLocalName());
        IRI method = vf.createIRI(VSD.method.getNameSpace(), VSD.method.getLocalName());
        IRI feature = vf.createIRI(VOCALS.feature.getNameSpace(), VOCALS.feature.getLocalName());
        IRI e = getEngineResource(engine, builder);

        String engine_base = e.getNamespace();

        Random random = new Random(0);

        Class<?>[] interfaces = engine.getInterfaces();
        Arrays.stream(interfaces)
                //.filter(i -> i.isAnnotationPresent(Feature.class))
                .forEach((Class<?> clazz) -> Arrays.stream(clazz.getMethods())
                        .forEachOrdered(method1 -> {

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

                            BNode service = vf.createBNode();
                            builder.add(e, hasService, service);

                            Feature feature_annotation =
                                    clazz.isAnnotationPresent(Feature.class) ?
                                            clazz.getAnnotation(Feature.class) :
                                            method1.getAnnotation(Feature.class);

                            RSPService annotation1 = method1.getAnnotation(RSPService.class);
                            String endpointstr = annotation1.endpoint();

                            for (Parameter p : method1.getParameters()) {
                                Param param = p.getAnnotation(Param.class);
                                BNode pbn = vf.createBNode();

                                if (param.uri()) {
                                    endpointstr += "/:" + param.name();
                                    builder.add(service, uri_param, pbn);
                                    builder.add(pbn, type, type_selector(p.getType()));
                                    builder.add(pbn, index, p.getName().replace("arg", ""));

                                } else {
                                    JsonObject serialize = serialize(param, p.getType()).getAsJsonObject();
                                    serialize.entrySet().forEach(entry -> {
                                        BNode bp = vf.createBNode();
                                        builder.add(service, body_param, bp);
                                        builder.add(bp, name, entry.getKey());

                                        Resource t = !entry.getValue().isJsonArray() ?
                                                vf.createIRI(entry.getValue().getAsString()) :
                                                vf.createIRI("array");

                                        builder.add(bp, RDF.TYPE, t);
                                        builder.add(bp, index, p.getName().replace("arg", ""));

                                    });

                                    builder.add(service, body, serialize.toString());
                                }

                                builder.add(pbn, name, param.name());

                            }

                            String ns = "UNKNOWN".equals(feature_annotation.ns()) ? engine_base + "/" : feature_annotation.ns();

                            Resource feat = vf.createIRI(ns, feature_annotation.name());

                            builder.add(service, feature, feat);
                            builder.add(service, name, feature_annotation.name());
                            builder.add(service, endpoint, endpointstr);
                            builder.add(service, method, annotation1.method().name());

                        }));

        return builder.build();
    }

    private static IRI getEngineResource(Class<?> engine, ModelBuilder model) {
        String uri = "";
        IRI e = null;
        IRI service = null;
        IRI base = vf.createIRI(VSD.base.getNameSpace(), VSD.base.getURI());
        if (engine.isAnnotationPresent(PublishingService.class)) {
            PublishingService cat = engine.getAnnotation(PublishingService.class);
            uri = "http://" + cat.host() + ":" + cat.port();
            e = vf.createIRI(StringUtils.removeLeadingAndTrailingSlashesFrom(uri));
            service = vf.createIRI(VSD.PublishingService.getNameSpace(), VSD.PublishingService.getURI());

        } else if (engine.isAnnotationPresent(ProcessingService.class)) {
            ProcessingService cat = engine.getAnnotation(ProcessingService.class);
            uri = "http://" + cat.host() + ":" + cat.port();
            e = vf.createIRI(StringUtils.removeLeadingAndTrailingSlashesFrom(uri));
            service = vf.createIRI(VSD.ProcessingService.getNameSpace(), VSD.ProcessingService.getURI());

        }
        if (engine.isAnnotationPresent(Catalog.class)) {
            Catalog cat = engine.getAnnotation(Catalog.class);
            e = vf.createIRI(StringUtils.removeLeadingAndTrailingSlashesFrom(uri));
            service = vf.createIRI(VSD.CatalogService.getNameSpace(), VSD.CatalogService.getURI());

        }

        model.defaultGraph().subject(e)
                .add(RDF.TYPE, service)
                .add(base, uri);

        return e;
    }

    public static JsonElement serialize(Param param, Class<?> c) {
        return serialize(param, new JsonObject(), "", c);
    }

    public static JsonElement serialize(Param param2, JsonElement obj, String name, Class<?> c) {
        if (c.isPrimitive() || String.class.equals(c)) {
            String n = name.isEmpty() ? param2.name() : name;
            if (obj.isJsonObject())
                ((JsonObject) obj).addProperty(n, type_selector(c).stringValue());
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

    public static IRI type_selector(Class<?> c) {
        if (String.class.equals(c))
            return XMLSchema.STRING;
        else if (Integer.class.equals(c))
            return XMLSchema.INTEGER;
        else if (Long.class.equals(c))
            return XMLSchema.LONG;
        else if (Boolean.class.equals(c))
            return XMLSchema.BOOLEAN;
        else if (Float.class.equals(c))
            return XMLSchema.FLOAT;
        else if (Double.class.equals(c))
            return XMLSchema.DOUBLE;
        else return XMLSchema.STRING;
    }

    public static List<Endpoint> fromVocals(Model model) {

        try {
            Set<Endpoint> set = new HashSet<>();

            String qstring = IOUtils.toString(VocalsFactoryRDF4J.class.getClassLoader().getResourceAsStream("endpoints.sparql"), Charset.defaultCharset());
            String uri_query = IOUtils.toString(VocalsFactoryRDF4J.class.getClassLoader().getResourceAsStream("uri_params.sparql"), Charset.defaultCharset());
            String body_query = IOUtils.toString(VocalsFactoryRDF4J.class.getClassLoader().getResourceAsStream("body.sparql"), Charset.defaultCharset());

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
