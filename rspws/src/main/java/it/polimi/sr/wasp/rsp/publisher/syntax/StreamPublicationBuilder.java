package it.polimi.sr.wasp.rsp.publisher.syntax;

import it.polimi.sr.wasp.rsp.publisher.StreamPublication;
import it.polimi.sr.wasp.rsp.publisher.model.WebChannel;
import it.polimi.sr.wasp.rsp.publisher.syntax.rdf.Subject;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Source;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import it.polimi.sr.wasp.server.model.description.DescriptorHashMap;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;

import java.util.*;
import java.util.stream.Collectors;

@ToString
public class StreamPublicationBuilder {
    private static final RDF rdf;
    private static Map<String, String> prefixes = new HashMap<>();
    private static Map<String, String> uris = new HashMap<>();
    private static Map<String, String> magics = new HashMap<>();

    public static final String TODO_PREFIX = "TODO_PREFIX";

    public static final String THIS = "{THIS}";
    public static final String STREAM = "{STREAM}";
    public static final String PUBLISHER = "{PUBLISHER}";
    public static final String SOURCE = "{SOURCE}";

    public static final String STREAM_DESCRIPTOR = "vocals:StreamDescriptor";
    public static final String RDFSTREAM = "vocals:RDFStream";
    public static final String PUBLISHING_SERVICE = "vsd:PublishingService";
    public static final String STREAM_ENDPOINT = "vocals:StreamEndpoint";

    public static final String JSONDL_ID = "@id";
    public static final String JSONDL_BASE = "@base";
    public static final String JSONDL_GRAPH = "@graph";
    public static final String JSONDL_TYPE = "@type";


    static {
        ServiceLoader<RDF> loader = ServiceLoader.load(RDF.class);
        Iterator<RDF> iterator = loader.iterator();
        rdf = iterator.next();

        magics.put("a", "rdf:type");

        prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        prefixes.put("vprov", "http://w3id.org/rsp/vocals-prov#");
        prefixes.put("vsd", "http://w3id.org/rsp/vocals-sd#");
        prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        prefixes.put("format", "http://www.w3.org/ns/formats/");
        prefixes.put("dcat", "http://www.w3.org/ns/dcat#");
        prefixes.put("vocals", "http://w3id.org/rsp/vocals#");

        uris.put("http://www.w3.org/2002/07/owl#", "owl");
        uris.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
        uris.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        uris.put("http://www.w3.org/2001/XMLSchema#", "xsd");
        uris.put("http://w3id.org/rsp/vocals-prov#", "vprov");
        uris.put("http://w3id.org/rsp/vocals-sd#", "vsd");
        uris.put("http://www.w3.org/2001/XMLSchema#", "xsd");
        uris.put("http://www.w3.org/ns/formats/", "format");
        uris.put("http://www.w3.org/ns/dcat#", "dcat");
        uris.put("http://w3id.org/rsp/vocals#", "vocals");

    }

    private List<QuadBuilder> quads = new ArrayList<>();

    public static String compact(String iri) {
        return uris.entrySet().stream().filter(e ->
                iri.contains(e.getKey())).findFirst()
                .map(e -> iri.replace(e.getKey(), e.getValue() + ":")).orElse(iri);
    }

    public static IRI resolve(String prefixedIRI) {
        if (prefixedIRI.contains("http")) {
            return rdf.createIRI(clean(prefixedIRI));
        } else if (prefixedIRI.contains(":")) {
            String[] prefix_n_res = prefixedIRI.split(":");
            String prefix = prefix(prefix_n_res[0]);
            if (!prefix.endsWith(URIUtils.SLASH) && !prefix.endsWith("#")) {
                prefix += URIUtils.SLASH;
            }
            return rdf.createIRI(prefix + prefix_n_res[1]);
        } else if (THIS.equals(prefixedIRI.toUpperCase())) {
            return rdf.createIRI(sgraph());
        } else if (STREAM.equals(prefixedIRI.toUpperCase())) {
            return rdf.createIRI(stream());
        } else if (PUBLISHER.equals(prefixedIRI.toUpperCase())) {
            return rdf.createIRI(publisher());
        } else if (SOURCE.equals(prefixedIRI.toUpperCase())) {
            return rdf.createIRI(source());
        }
        if ("a".equals(prefixedIRI)) {
            return resolve(magics.get(prefixedIRI));
        } else return rdf.createIRI(clean(prefixedIRI));
    }

    private static String prefix(String prefix_n_re) {
        return prefixes.get(prefix_n_re);
    }


    private String base() {
        return prefixes.get("");
    }

    private static String source() {
        return magics.get(SOURCE);
    }

    private static String sgraph() {
        return magics.get(THIS);
    }

    private static String stream() {
        return magics.get(STREAM);
    }

    private static String publisher() {
        return magics.get(PUBLISHER);
    }

    public static boolean isResource(String res) {
        return res.contains("<") || res.contains(">") || !res.contains("^^");
    }

    private static String clean(String baseURI) {
        return baseURI.replace(">", "").replace("<", "");
    }

    public StreamPublicationBuilder setBaseURI(String baseURI) {
        magics.put(PUBLISHER, clean(baseURI));
        prefixes.put("", resolve(clean(baseURI)).getIRIString());
        return this;
    }

    public StreamPublicationBuilder addPrefix(String prefix, String uri) {
        if (uri.equals(prefix)) {
            prefixes.put(prefixes.remove(TODO_PREFIX), clean(uri));
        } else {
            if (TODO_PREFIX.equals(prefix))
                uri = uri.replace(":", "");
            prefixes.put(prefix, uri);
        }
        return this;
    }

    public StreamPublicationBuilder setId(String id) {
        IRI id2 = resolve(clean(id).replace(":", ":streams/"));
        magics.put(STREAM, id2.getIRIString());
        magics.put(THIS, resolve(id2.getIRIString()).getIRIString());
        return this;
    }


    public StreamPublicationBuilder setSource(String source) {
        magics.put(SOURCE, clean(source));
        return this;
    }

    public StreamPublicationBuilder addQuad(QuadBuilder pop) {
        quads.add(pop);
        return this;
    }

    public Descriptor describe() {
        DescriptorHashMap stream = new DescriptorHashMap() {
            HashMap<String, Object> cts = new HashMap<>();

            @Override
            public Map<String, Object> context() {
                return cts;
            }

            {
                put(JSONDL_ID, stream());
            }
        };


        List<DescriptorHashMap> metadata = quads.stream().map(QuadBuilder::describe).flatMap(Collection::stream).collect(Collectors.toList());

        List<Descriptor> endpoints = new ArrayList<>();

        DescriptorHashMap publisher = new DescriptorHashMap() {
            @Override
            public Map<String, Object> context() {
                return new HashMap<>();
            }

            {
                put(JSONDL_ID, publisher());
            }
        };

        return new DescriptorHashMap() {

            String stream1 = stream();
            String sgraph = sgraph();
            String publisher1 = publisher();

            @Override
            public Map<String, Object> context() {
                return new LinkedHashMap<String, Object>() {
                    {
                        prefixes.forEach(this::put);

                        put(JSONDL_BASE, remove("") + URIUtils.SLASH);
                        put("dcat:accessURL", new LinkedHashMap<String, Object>() {
                            {
                                put(JSONDL_TYPE, "xsd:string");
                            }
                        });

                        endpoints.stream()
                                .flatMap(descriptor -> descriptor.context().entrySet().stream())
                                .forEach(e -> put(e.getKey(), e.getValue()));
                    }
                };
            }

            {
                put(JSONDL_ID, sgraph());

                metadata.forEach(dhm -> {

                    Object id = dhm.get(JSONDL_ID);
                    Object type = dhm.get(JSONDL_TYPE);

                    if (sgraph.equals(id) && resolve(STREAM_DESCRIPTOR).getIRIString().equals(type)) {
                        dhm.forEach(this::put);
                        dhm.context().forEach(this.context()::put);
                    } else if (stream1.equals(id) && resolve(RDFSTREAM).getIRIString().equals(type)) {
                        dhm.forEach(stream::put);
                        dhm.context().forEach(stream.context()::put);
                    } else if (publisher1.equals(id) && resolve(PUBLISHING_SERVICE).getIRIString().equals(type)) {
                        dhm.forEach(publisher::put);
                        dhm.context().forEach(publisher.context()::put);
                    } else if (resolve(STREAM_ENDPOINT).getIRIString().equals(type)) {
                        endpoints.add(dhm);
                        dhm.context().forEach(this.context()::put);
                    }
                });

                if (endpoints.size() > 0) {
                    stream.put(resolve("vocals:hasEndpoint").getIRIString(), endpoints.toArray(new Descriptor[endpoints.size()]));
                }

                put("dcat:dataset", stream);
                stream.context().forEach(this.context()::put);

                put("vsd:publishedBy", publisher);
                publisher.context().forEach(this.context()::put);
            }
        };
    }

    public Built build() {

        String base = base();
        String sgraph = sgraph();
        String s = source();
        String stream = stream();

        Channel channel = new WebChannel(base, stream, s, describe());

        StreamPublication streamPublication = new StreamPublication(channel, sgraph);

        channel.add(streamPublication);

        Source source = new Source() {

            List<Channel> channels = Collections.singletonList(channel);

            @Override
            public Channel add(Channel t) {
                channels.add(t);
                return t;
            }

            @Override
            public void stop() {

            }
        };

        return new Built(source, channel, streamPublication);
    }

    @NoArgsConstructor
    public static class QuadBuilder {

        @Getter
        private IRI graph;

        private List<Subject> subjects = new ArrayList<>();

        public QuadBuilder(String graph) {
            this.graph = resolve(graph);
        }

        public QuadBuilder subject(String subj) {
            subjects.add(new Subject(resolve(subj)));
            return this;
        }

        public QuadBuilder predicate(String predicate) {
            subjects.get(subjects.size() - 1).predicate(resolve(predicate));
            return this;
        }

        public QuadBuilder object(String object) {
            if (isResource(object)) {
                subjects.get(subjects.size() - 1).object(resolve(object));
            } else {
                String[] split = object.split("\\^\\^");
                subjects.get(subjects.size() - 1).object(rdf.createLiteral(split[0], resolve(split[1])));
            }
            return this;
        }

        public List<Quad> quads() {
            return subjects.stream().flatMap(s -> s.getPredicates().stream().flatMap
                    (p -> p.getObjects().stream().map(o -> rdf.createQuad(graph, s.node, p.node, o)))).collect(Collectors.toList());
        }

        public List<DescriptorHashMap> describe() {

            List<DescriptorHashMap> res = subjects.stream().map(this::describe).collect(Collectors.toList());

            if (graph != null) {
                return Collections.singletonList(new DescriptorHashMap() {
                    @Override
                    public Map<String, Object> context() {
                        HashMap<String, Object> ctx = new HashMap<>();
                        res.forEach(r -> r.context().forEach(ctx::put));
                        return ctx;
                    }

                    {
                        put(JSONDL_ID, graph.getIRIString());
                        put(JSONDL_GRAPH, subjects.stream().map(subject -> describe(subject)).toArray(DescriptorHashMap[]::new));

                    }

                });
            } else {

                return res;

            }


        }

        private DescriptorHashMap describe(Subject subject) {
            return new DescriptorHashMap() {
                HashMap<String, Object> stringObjectHashMap = new HashMap<>();

                @Override
                public Map<String, Object> context() {
                    return stringObjectHashMap;
                }

                {
                    put(JSONDL_ID, subject.node.getIRIString());

                    subject.getPredicates().forEach(predicate ->
                            predicate.getObjects().forEach(obj -> {
                                String p = predicate.node.getIRIString();

                                if (obj instanceof Literal) {
                                    Literal l = (Literal) obj;
                                    put(compact(p), l.getLexicalForm().replace("\"", ""));
                                    context().put(compact(p), new LinkedHashMap<String, Object>() {
                                        {
                                            put(JSONDL_TYPE, l.getDatatype().getIRIString());
                                        }
                                    });
                                } else if (obj instanceof IRI) {
                                    put(p.contains("type") ? JSONDL_TYPE : compact(p), ((IRI) obj).getIRIString());
                                }

                            }));
                }
            };
        }


        @Override
        public String
        toString() {
            return "QuadBuilder: " + Objects.toString(describe());
        }
    }

    @RequiredArgsConstructor
    public class Built {

        public final Source source;
        public final Channel channel;
        public final StreamPublication task;

    }
}
