package it.polimi.rsp.vocals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.rsp.server.HttpMethod;
import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;
import it.polimi.rsp.vocals.annotations.services.Catalog;
import it.polimi.rsp.vocals.annotations.services.ProcessingService;
import it.polimi.rsp.vocals.annotations.services.PublishingService;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.apache.commons.rdf.simple.Types;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import spark.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.*;

@Log
public class VocalsUtils {

    public static final Map<String, String> prefixMap = new HashMap<>();

    public static final RDF rdf = new SimpleRDF();
    public static final String vsd = "http://w3id.org/rsp/vocals-sd#";
    public static final IRI hasService = rdf.createIRI(vsd + "hasService");
    public static final IRI processingService = rdf.createIRI(vsd + "ProcessingService");
    public static final IRI publishingService = rdf.createIRI(vsd + "PublishingService");
    public static final IRI catalogService = rdf.createIRI(vsd + "CatalogService");
    public static final IRI base = rdf.createIRI(vsd + "base");
    public static final IRI type = rdf.createIRI(vsd + "type");
    public static final IRI uri_param = rdf.createIRI(vsd + "uri_param");
    public static final IRI body_param = rdf.createIRI(vsd + "body_param");
    public static final IRI body = rdf.createIRI(vsd + "uri");
    public static final IRI name = rdf.createIRI(vsd + "name");
    public static final IRI index = rdf.createIRI(vsd + "index");
    public static final IRI feature = rdf.createIRI(vsd + "feature");
    public static final IRI endpoint = rdf.createIRI(vsd + "endpoint");
    public static final IRI method = rdf.createIRI(vsd + "method");

    public static final IRI a = rdf.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

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
        Arrays.stream(interfaces).filter(i -> i.isAnnotationPresent(Feature.class))
                .forEach((Class<?> feature) -> {

                    Method method = feature.getMethods()[0];

//                    Class<?> returnType = method.getReturnType();
//                    if (returnType.isAnnotationPresent(Exposed.class)) {
//                        Exposed annotation = returnType.getAnnotation(Exposed.class);
//                        String name = annotation.name();
//                        String endpt = URIUtils.SLASH + name;
//                        createGetterEndpoint(model, e, name, endpt, "");
//                        Arrays.stream(returnType.getFields())
//                                .filter(field -> field.isAnnotationPresent(Key.class))
//                                .map(Field::getName).forEach(field ->
//                                createGetterEndpoint(model, e, name, URIUtils.addParam(endpt, field), field));
//                    }
//
//                    if (returnType.isAnnotationPresent(Deletable.class)) {
//                        Deletable del = returnType.getAnnotation(Deletable.class);
//                        String delname = del.name();
//                        String endpt = URIUtils.SLASH + delname;
//                        createDeleteEndpoint(model, e, delname, endpt, "");
//                        Arrays.stream(returnType.getFields())
//                                .filter(field -> field.isAnnotationPresent(Key.class))
//                                .map(Field::getName).forEach(field ->
//                                createDeleteEndpoint(model, e, delname, URIUtils.addParam(endpt, field), field));
//                    }

                    Resource service = model.createResource("feature" + random.nextInt(interfaces.length * 2));
                    e.addProperty(VSD.hasService, service);

                    Feature feature_annotation = feature.getAnnotation(Feature.class);

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
                });

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


    private static IRI getEngineResource2(Class<?> engine, Graph g) {
        Graph graph = rdf.createGraph();
        String b = "";
        IRI p = null;
        if (engine.isAnnotationPresent(PublishingService.class)) {
            PublishingService cat = engine.getAnnotation(PublishingService.class);
            b = "http://" + cat.host() + ":" + cat.port();
            p = publishingService;
        } else if (engine.isAnnotationPresent(ProcessingService.class)) {
            ProcessingService cat = engine.getAnnotation(ProcessingService.class);
            b = "http://" + cat.host() + ":" + cat.port();
            p = processingService;
        }
        if (engine.isAnnotationPresent(Catalog.class)) {
            Catalog cat = engine.getAnnotation(Catalog.class);
            b = "http://" + cat.host() + ":" + cat.port();
            p = catalogService;
        }

        IRI e = rdf.createIRI(StringUtils.removeLeadingAndTrailingSlashesFrom(b));
        graph.add(rdf.createTriple(e, a, p));
        graph.add(rdf.createTriple(e, base, rdf.createLiteral(b)));

        return e;
    }

    public static Graph toVocals2(final Class<?> engine) {

        Graph graph = rdf.createGraph();
        IRI engine_uri = getEngineResource2(engine, graph);

        Class<?>[] interfaces = engine.getInterfaces();
        Arrays.stream(interfaces).filter(i -> i.isAnnotationPresent(Feature.class))
                .forEach((Class<?> f) -> {

                    BlankNode s = rdf.createBlankNode();
                    graph.add(rdf.createTriple(engine_uri, hasService, s));

                    Method m = f.getMethods()[0];
                    Feature feature_annotation = f.getAnnotation(Feature.class);

                    RSPService annotation1 = m.getAnnotation(RSPService.class);
                    String end = annotation1.endpoint();

                    for (Parameter p : m.getParameters()) {
                        Param param = p.getAnnotation(Param.class);

                        BlankNode pbn = rdf.createBlankNode();

                        if (param.uri()) {
                            end += "/:" + param.name();
                            graph.add(rdf.createTriple(s, uri_param, pbn));
                            Resource resource = type_selector(p.getType());
                            graph.add(rdf.createTriple(pbn, type, rdf.createIRI(resource.getURI())));
                        } else {
                            graph.add(rdf.createTriple(s, body_param, pbn));
                            graph.add(rdf.createTriple(pbn, body, rdf.createLiteral(serialize(param, p.getType()).toString(), Types.XSD_STRING)));
                        }

                        graph.add(rdf.createTriple(pbn, name, rdf.createLiteral(param.name(), Types.XSD_STRING)));
                        graph.add(rdf.createTriple(pbn, index, rdf.createLiteral(p.getName().replace("arg", ""), Types.XSD_INTEGER)));

                    }

                    String ns = "UNKNOWN".equals(feature_annotation.ns()) ? engine_uri.getIRIString() + "/" : feature_annotation.ns();

                    IRI feat = rdf.createIRI(ns + feature_annotation.name());

                    graph.add(rdf.createTriple(s, feature, feat));
                    graph.add(rdf.createTriple(s, name, rdf.createLiteral(feature_annotation.name(), Types.XSD_STRING)));
                    graph.add(rdf.createTriple(s, endpoint, rdf.createLiteral(end, Types.XSD_STRING)));
                    graph.add(rdf.createTriple(s, method, rdf.createLiteral(annotation1.method().name(), Types.XSD_STRING)));

                });


        return graph;
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

            String qstring = IOUtils.toString(VocalsUtils.class.getClassLoader().getResourceAsStream("endpoints.sparql"), Charset.defaultCharset());
            String uri_query = IOUtils.toString(VocalsUtils.class.getClassLoader().getResourceAsStream("uri_params.sparql"), Charset.defaultCharset());
            String body_query = IOUtils.toString(VocalsUtils.class.getClassLoader().getResourceAsStream("body.sparql"), Charset.defaultCharset());

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
