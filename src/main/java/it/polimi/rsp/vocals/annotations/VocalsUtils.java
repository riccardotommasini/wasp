package it.polimi.rsp.vocals.annotations;

import com.google.gson.JsonObject;
import it.polimi.rsp.Endpoint;
import it.polimi.rsp.SpecUtils;
import it.polimi.rsp.server.HttpMethod;
import it.polimi.rsp.vocals.VOCALS;
import it.polimi.rsp.vocals.VSD;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
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

    static {
        prefixMap.put("vocals", VOCALS.getUri());
        prefixMap.put("vsd", VSD.getUri());
        prefixMap.put("xsd", XSD.getURI());
        prefixMap.put("frmt", "http://www.w3.org/ns/formats/");
    }


    public static Model toVocals(Object o, String name, String base) {
        return toVocals(o.getClass(), name, base);
    }

    public static Model toVocals(final Class<?> engine, final String name, final String base) {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(prefixMap);

        final Resource e = model.createResource(StringUtils.removeLeadingAndTrailingSlashesFrom(base))
                .addProperty(RDF.type, VSD.ProcessingService)
                .addProperty(VSD.name, name)
                .addProperty(VSD.base, engine.getAnnotation(Base.class).base());

        Arrays.stream(engine.getInterfaces()).filter(i -> i.isAnnotationPresent(Feature.class))
                .forEach((Class<?> feature) -> {

                    Resource bn = model.createResource();
                    e.addProperty(VSD.hasService, bn);

                    Method method = feature.getMethods()[0];
                    Feature feature_annotation = feature.getAnnotation(Feature.class);

                    RSPService annotation1 = method.getAnnotation(RSPService.class);
                    String endpoint = annotation1.endpoint();

                    Resource feat = model.createProperty(base, feature_annotation.name());
                    bn.addProperty(VOCALS.feature, feat);
                    bn.addProperty(VSD.name, feature_annotation.name());

                    for (Parameter p : method.getParameters()) {
                        Param param = p.getAnnotation(Param.class);

                        Resource pbn = model.createResource();

                        if (param.uri()) {
                            endpoint += "/:" + param.name();
                            bn.addProperty(VSD.uri_param, pbn);
                            pbn.addProperty(VSD.type, type_selector(p.getType()));
                        } else {
                            bn.addProperty(VSD.body_param, pbn);
                            pbn.addProperty(VSD.body, serialize(p.getType()).toString());
                        }
                        pbn.addProperty(VSD.name, param.name());
                        pbn.addProperty(VSD.index, p.getName().replace("arg", ""));
                    }
                    bn.addProperty(VSD.endpoint, endpoint);
                    bn.addProperty(VSD.method, annotation1.method().name());
                });

        return model;
    }

    public static JsonObject serialize(Class<?> c) {
        return serialize(new JsonObject(), "", c);
    }

    public static JsonObject serialize(final JsonObject obj, String name, Class<?> c) {
        if (c.isPrimitive() || String.class.equals(c)) {
            obj.addProperty(name, type_selector(c));
        } else {
            Arrays.stream(c.getFields())
                    .forEach(field -> serialize(obj, field.getName(), field.getType()));

            Arrays.stream(c.getMethods())
                    .filter(m -> m.getName().
                            startsWith("set"))
                    .filter(method -> method.getParameterCount() == 1)
                    .forEach(method ->
                            serialize(obj, method.getName().replace("set", ""), method.getParameters()[0].getType()));
        }

        return obj;
    }

    public static String type_selector(Class<?> c) {
        if (String.class.equals(c))
            return XSD.xstring.getURI();
        else if (Integer.class.equals(c))
            return XSD.integer.getURI();
        else if (Long.class.equals(c))
            return XSD.xlong.getURI();
        else if (Boolean.class.equals(c))
            return XSD.xboolean.getURI();
        else if (Float.class.equals(c))
            return XSD.xfloat.getURI();
        else if (Double.class.equals(c))
            return XSD.xdouble.getURI();
        return "";
    }

    public static List<Endpoint> fromVocals(Model model) {

        try {
            List<Endpoint> list = new ArrayList<>();

            String qstring = IOUtils.toString(SpecUtils.class.getClassLoader().getResourceAsStream("endpoints.sparql"), Charset.defaultCharset());
            String uri_query = IOUtils.toString(SpecUtils.class.getClassLoader().getResourceAsStream("uri_params.sparql"), Charset.defaultCharset());
            String body_query = IOUtils.toString(SpecUtils.class.getClassLoader().getResourceAsStream("body.sparql"), Charset.defaultCharset());

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
                    String body = next.get("?body").toString();
                    int index = Integer.parseInt(next.get("?index").toString());
                    params.add(new Endpoint.Par(body, index, false));
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

                list.add(new Endpoint(
                        s.get("?name").toString(),
                        s.get("?endpoint").toString(),
                        HttpMethod.valueOf(s.get("?method").toString()),
                        feature,
                        params1));
            }

            return list;
        } catch (IOException e) {
            return new ArrayList<>();
        }

    }


}
