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
import lombok.extern.java.Log;
import org.apache.commons.rdf.api.*;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.apache.commons.rdf.simple.Types;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import spark.utils.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class VocalsFactoryRDFCommons {

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
    /*SELECT ?name ?endpoint ?method ?feature  ?service
     WHERE {

    ?engine vsd:hasService ?service .

    ?service vsd:name ?name ;
             vsd:method ?method ;
             vsd:endpoint ?endpoint .

    OPTIONAL {  ?service vocals:feature ?feature . }
    }*/

    public static List<Endpoint> fromVocals(Graph g) {

        Set<Endpoint> set = new HashSet<>();


        List<Triple> stream = g.stream()
                .filter(t -> hasService.equals(t.getPredicate())).collect(Collectors.toList());
        Map<RDFTerm, List<Triple>> res = g.stream()
                .filter(t -> hasService.equals(t.getPredicate()))
                .map(Triple::getObject)
                .flatMap(service -> g.stream()
                        .filter(t -> t.getSubject().equals(service))
                        .filter(t -> name.equals(t.getPredicate()) ||
                                method.equals(t.getPredicate()) ||
                                feature.equals(t.getPredicate()) ||
                                endpoint.equals(t.getPredicate())))
                .collect(Collectors.groupingBy(Triple::getSubject));

        res.entrySet().stream().filter(e -> e.getValue().size() == 4).forEach(e -> {
                    final RDFTerm cservice = e.getKey();
                    RDFTerm cfeature = null;
                    RDFTerm cendpoint = null;
                    RDFTerm cmethod = null;
                    RDFTerm cname = null;

                    for (Triple triple : e.getValue()) {
                        if (triple.getPredicate().equals(feature)) {
                            cfeature = triple.getObject();
                        } else if (triple.getPredicate().equals(name)) {
                            cname = triple.getObject();
                        } else if (triple.getPredicate().equals(endpoint)) {
                            cendpoint = triple.getObject();
                        } else if (triple.getPredicate().equals(method)) {
                            cmethod = triple.getObject();
                        }
                    }

                        /*SELECT DISTINCT ?name ?index
                        WHERE  {
                            ?service vocals:feature ?feature;
                                      vsd:uri_param ?uri_param .
                            ?uri_param vsd:index ?index ;
                                       vsd:name ?name .
                        }
                        ORDER BY ?index*/

                    final RDFTerm ff = cfeature;

                    Stream<RDFTerm> uri_params = g.stream()
                            .filter(t -> t.getSubject().equals(cservice))
                            .filter(t -> t.getObject().equals(ff))
                            .filter(t -> t.getPredicate().equals(uri_param))
                            .map(Triple::getObject);

                    Map<RDFTerm, List<Triple>> params_map = uri_params.flatMap(p -> g.stream()
                            .filter(t -> t.getSubject().equals(p))
                            .filter(t -> name.equals(t.getPredicate()) ||
                                    index.equals(t.getPredicate())))
                            .collect(Collectors.groupingBy(Triple::getSubject));

                    List<Endpoint.Par> params = new ArrayList<>();

                    params_map.entrySet().stream().filter(ee -> e.getValue().size() == 2).forEach(ee -> {
                        final RDFTerm param = ee.getKey();
                        String ccname = "";
                        int cindex = 0;

                        for (Triple triple : ee.getValue()) {
                            if (triple.getPredicate().equals(index)) {
                                cindex = Integer.parseInt(triple.getObject().ntriplesString());
                            } else if (triple.getPredicate().equals(name)) {
                                ccname = triple.getObject().ntriplesString();
                            }
                        }
                        params.add(new Endpoint.Par(ccname, cindex, true));
                    });





                        /*PREFIX vsd: <http://w3id.org/rsp/vocals-sd#>
                        PREFIX vocals: <http://w3id.org/rsp/vocals#>

                        SELECT DISTINCT ?name ?index
                        WHERE  {
                            ?service vocals:feature ?feature ;
                                      vsd:body_param ?param .
                            ?param vsd:name ?name ;
                                   vsd:index ?index .
                        }
                        ORDER BY ?index*/


                    Stream<RDFTerm> body_params = g.stream()
                            .filter(t -> t.getSubject().equals(cservice))
                            .filter(t -> t.getObject().equals(ff))
                            .filter(t -> t.getPredicate().equals(body_param))
                            .map(Triple::getObject);

                    params_map = uri_params.flatMap(p -> g.stream()
                            .filter(t -> t.getSubject().equals(p))
                            .filter(t -> name.equals(t.getPredicate()) ||
                                    index.equals(t.getPredicate())))
                            .collect(Collectors.groupingBy(Triple::getSubject));

                    params_map.entrySet().stream().filter(ee -> e.getValue().size() == 2).forEach(ee -> {
                        final RDFTerm param = ee.getKey();
                        String ccname = "";
                        int cindex = 0;

                        for (Triple triple : ee.getValue()) {
                            if (triple.getPredicate().equals(index)) {
                                cindex = Integer.parseInt(triple.getObject().ntriplesString());
                            } else if (triple.getPredicate().equals(name)) {
                                ccname = triple.getObject().ntriplesString().replace("\\", "");
                            }
                        }
                        params.add(new Endpoint.Par(ccname, cindex, false));
                    });

                    params.sort((o1, o2) -> o1.index < o2.index ? -1 : (
                            o1.index == o2.index ? 0 : -1));


                    Endpoint.Par[] params1 = new Endpoint.Par[params.size()];

                    for (int i = 0; i < params.size(); i++) {
                        params1[i] = params.get(i);
                    }

                    set.add(new Endpoint(
                            cname.ntriplesString(),
                            cendpoint.ntriplesString(),
                            HttpMethod.valueOf(cmethod.ntriplesString()),
                            cfeature.ntriplesString(),
                            params1));
                }
        );

        return new ArrayList<>(set);


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


}
