package it.polimi.sr.wasp.rsp.model;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.concept.Task;
import it.polimi.sr.wasp.server.model.concept.calls.Caller;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import it.polimi.sr.wasp.server.model.description.DescriptorHashMap;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.*;

@Log4j2
public class DataStream implements Channel {

    public String id;
    public String source;
    private String base;

    protected List<Sink> sinks = new ArrayList<>();
    protected List<Task> tasks = new ArrayList<>();


    public DataStream(String base, String id, String uri) {
        this.id = id;
        this.source = uri;
        this.base = base;
    }

    @Override
    public String toString() {
        try {
            return getJson();
        } catch (IOException e) {
            return "\"@context\": {\n" +
                    "    \"@base\":\"http://localhost:8181/csparql\",\n" +
                    "    \"vocals\": \"http://w3id.org/rsp/vocals#\",\n" +
                    "    \"vprov\": \"http://w3id.org/rsp/vocals-prov#\",\n" +
                    "    \"vsd\": \"http://w3id.org/rsp/vocals-sd#\",\n" +
                    "    \"xsd\": \"http://www.w3.org/2001/XMLSchema#\",\n" +
                    "    \"dcat\":\"\"\n" +
                    "  },\n" +
                    "  \n" +
                    "  \"@id\": \"" + iri() + "\",\n" +
                    "  \"@type\":\"vocals:RDFStream\",\n" +
                    "  \"dcat:title\": \"" + iri() + "\",\n" +
                    "  \"vocals:hasEndpoint\": {\n" +
                    "    \"@type\": \"vocals:StreamEndpoint\",\n" +
                    "    \"dcat:accessURL\": \"ws://example.org/traffic/milan\"\n" +
                    "  }";
        }

    }

    private String getJson() throws IOException {
        JsonLdOptions options = new JsonLdOptions();
        Map<String, Object> context = new HashMap<>();
        context.put("@base", base + URIUtils.SLASH);
        context.put("vocals", "http://w3id.org/rsp/vocals#");
        context.put("vprov", "http://w3id.org/rsp/vocals-prov#");
        context.put("vsd", "http://w3id.org/rsp/vocals-sd#");
        context.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        context.put("format", "http://www.w3.org/ns/formats/");
        context.put("dcat", "https://www.w3.org/TR/vocab-dcat/");
        context.put("dcat:accessURL", new LinkedHashMap<String, Object>() {
            {
                put("@type", "xsd:string");
            }
        });

        Map<String, Object> descriptor = new HashMap<>();
        Map<String, Object> stream = new HashMap<>();
        stream.put("@id", iri());
        stream.put("@type", "vocals:RDFStream");
        descriptor.put("@type", "vocals:StreamDescriptor");
        descriptor.put("dcat:dataset", stream);

        if (source.contains("ws://")) {
            stream.put("vocals:hasEndpoint", new DescriptorHashMap() {
                {
                    put("@type", "vocals:StreamEndpoint");
                    put("dcat:accessURL", source);
                    put("dcat:format", "frmt:JSON-LD");
                    put("vsd:publishedBy", base);
                }
            });
        }

        sinks.stream().
                filter(sink -> !sink.describe().empty()).
                forEach(sink -> stream.put("vocals:hasEndpoint", sink.describe()));

        String s = JsonUtils.toPrettyString(JsonLdProcessor.compact(descriptor, context, options));
        return s;
    }


    @Override
    public void yield(String message) {
        log.debug(" Yield message " + message);
        tasks.forEach(t -> t.yield(message));
        sinks.forEach(sink -> sink.yield(message));
    }

    @Override
    public void await(Caller s, String m) {
        log.debug(id + " got a message " + m + "from " + s);
        yield(m);
    }

    @Override
    public void add(Sink observer) {
        sinks.add(observer);
    }

    @Override
    public Channel add(Channel c) {
        //TODO
        return c;
    }

    @Override
    public Channel apply(Task t) {
        tasks.add(t);
        return this;
    }

    @Override
    public Descriptor describe() {

        Map<String, Object> context = new HashMap<>();
        context.put("@base", base + URIUtils.SLASH);
        context.put("vocals", "http://w3id.org/rsp/vocals#");
        context.put("vprov", "http://w3id.org/rsp/vocals-prov#");
        context.put("vsd", "http://w3id.org/rsp/vocals-sd#");
        context.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        context.put("format", "http://www.w3.org/ns/formats/");
        context.put("dcat", "https://www.w3.org/TR/vocab-dcat/");
        context.put("dcat:accessURL", new LinkedHashMap<String, Object>() {
            {
                put("@type", "xsd:string");
            }
        });

        Map<String, Object> stream = new HashMap<>();
        stream.put("@id", iri());
        stream.put("@type", "vocals:RDFStream");

        if (source.contains("ws://")) {
            stream.put("vocals:hasEndpoint", new DescriptorHashMap() {
                {
                    put("@type", "vocals:StreamEndpoint");
                    put("dcat:accessURL", source);
                    put("dcat:format", "frmt:JSON-LD");
                    put("vsd:publishedBy", base);
                }
            });
        }

        sinks.stream().
                filter(sink -> !sink.describe().empty()).
                forEach(sink -> stream.put("vocals:hasEndpoint", sink.describe()));

        return new DescriptorHashMap() {{
            put("@type", "vocals:StreamDescriptor");
            put("dcat:dataset", stream);
        }};
    }

    @Override
    public String iri() {
        return id;
    }

}
