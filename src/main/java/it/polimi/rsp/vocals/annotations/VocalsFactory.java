package it.polimi.rsp.vocals.annotations;

import it.polimi.rsp.vocals.VOCALS;
import it.polimi.rsp.vocals.VPROV;
import it.polimi.rsp.vocals.VSD;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VocalsFactory {

    public static final Map<String, String> prefixMap = new HashMap<>();

    static {
        prefixMap.put("vocals", VOCALS.getUri());
        prefixMap.put("vsd", VSD.getUri());
        prefixMap.put("xsd", VPROV.getUri());
        prefixMap.put("xsd", XSD.getURI());
        prefixMap.put("frmt", "http://www.w3.org/ns/formats/");
    }

    public static Model toVocals(Object o, String name, String base) {
        return toVocals(o.getClass(), name, base);
    }

    public static Model toVocals(final Class<?> engine, final String name, final String base) {

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(prefixMap);
        model.setNsPrefix("", base);


        final Resource e = model.createResource(base + name)
                .addProperty(RDF.type, VSD.ProcessingService)
                .addProperty(VSD.name, name)
                .addProperty(VSD.base, engine.getAnnotation(Base.class).base());

        Arrays.stream(engine.getInterfaces()).filter(i -> i.isAnnotationPresent(Feature.class))
                .forEach((Class<?> feature) -> {

                    Resource bn = model.createResource();
                    e.addProperty(VSD.hasService, bn);

                    Method method = feature.getMethods()[0];
                    Feature annotation = feature.getAnnotation(Feature.class);

                    RSPService annotation1 = method.getAnnotation(RSPService.class);
                    String endpoint = annotation1.endpoint();

                    Resource feat = model.createProperty(base, annotation.vocals());
                    bn.addProperty(VOCALS.feature, feat);
                    bn.addProperty(VSD.name, annotation.vocals());

                    for (Parameter p : method.getParameters()) {
                        Resource pbn = model.createResource();
                        bn.addProperty(VSD.params, pbn);

                        Param param = p.getAnnotation(Param.class);
                        pbn.addProperty(VSD.name, param.name());
                        pbn.addProperty(VSD.index, p.getName().replace("arg", ""));

                        if (param.uri()) {
                            endpoint += "/:" + param.name();
                            pbn.addProperty(RDF.type, VSD.uri_param);
                        } else
                            pbn.addProperty(RDF.type, VSD.body_param);
                    }
                    bn.addProperty(VSD.endpoint, endpoint);
                    bn.addProperty(VSD.method, annotation1.method().name());
                });

        return model;
    }

}
