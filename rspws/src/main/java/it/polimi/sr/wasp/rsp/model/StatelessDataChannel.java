package it.polimi.sr.wasp.rsp.model;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import it.polimi.sr.wasp.VOCABS;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.concept.tasks.AsynchTask;
import it.polimi.sr.wasp.server.model.concept.tasks.SynchTask;
import it.polimi.sr.wasp.server.model.concept.tasks.Task;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import it.polimi.sr.wasp.server.model.description.DescriptorHashMap;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.*;

@Log4j2
public class StatelessDataChannel implements Channel {

    public String id;
    public String source;
    private String base;

    protected List<Sink> sinks = new ArrayList<>();
    protected List<AsynchTask> synch_task = new ArrayList<>();
    protected List<SynchTask> asynch_task = new ArrayList<>();
    protected List<Task> tasks = new ArrayList<>();

    public StatelessDataChannel(String base, String id, String source) {
        this.base = base;
        this.id = id;
        this.source = source;
    }

    @Override
    public String toString() {
        try {
            return getJson();
        } catch (IOException e) {
            return "Error";
        }

    }

    private String getJson() throws IOException {
        Descriptor describe = describe();
        Map<String, Object> context = describe.context();
        JsonLdOptions opts = new JsonLdOptions();
        opts.setPruneBlankNodeIdentifiers(true);
        return JsonUtils.toPrettyString(JsonLdProcessor.compact(describe, context, opts));
    }

    @Override
    public Channel put(Object o) {
        String message = o.toString();
        log.debug(" Yield message " + message);
        synch_task.forEach(t -> t.yield(this, message));
        sinks.forEach(sink -> sink.await(message));
        asynch_task.forEach(t -> t.await(message));
        return this;
    }

    @Override
    public Channel add(Sink observer) {
        sinks.add(observer);
        return this;
    }

    @Override
    public Channel add(Channel c) {
        //TODO
        return this;
    }

    @Override
    public Channel add(Task t) {
        tasks.add(t);
        return this;
    }

    @Override
    public Channel add(SynchTask t) {
        tasks.add(t);
        asynch_task.add(t);
        return this;
    }

    @Override
    public Channel add(AsynchTask t) {
        tasks.add(t);
        synch_task.add(t);
        return this;
    }

    @Override
    public Descriptor describe() {

        Map<String, Object> stream = new HashMap<>();
        stream.put("@id", iri());
        stream.put("@type", "vocals:RDFStream");

        List<Descriptor> endpoints = new ArrayList<>();

        if (source.contains("ws://")) {
            Map<String, Object> context = new HashMap<>();
            endpoints.add(new DescriptorHashMap() {
                @Override
                public Map<String, Object> context() {
                    context.put("@base", base + URIUtils.SLASH);
                    context.put(VOCABS.VPROV.prefix, VOCABS.VPROV.uri);
                    context.put(VOCABS.VSD.prefix, VOCABS.VSD.uri);
                    context.put(VOCABS.FORMAT.prefix, VOCABS.FORMAT.uri);
                    context.put(VOCABS.XSD.prefix, VOCABS.XSD.uri);
                    context.put(VOCABS.DCAT.prefix, VOCABS.DCAT.uri);
                    context.put(VOCABS.VOCALS.prefix, VOCABS.VOCALS.uri);
                    return context;
                }

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
                forEach(sink -> endpoints.add(sink.describe()));


        if (endpoints.size() > 0) {
            stream.put("vocals:hasEndpoint", endpoints.toArray(new Descriptor[endpoints.size()]));
        }


        return new DescriptorHashMap() {
            @Override
            public Map<String, Object> context() {
                return new LinkedHashMap<String, Object>() {
                    {
                        put(VOCABS.DCAT.prefix, VOCABS.DCAT.uri);
                        put(VOCABS.VOCALS.prefix, VOCABS.VOCALS.uri);
                        endpoints.stream()
                                .flatMap(descriptor -> descriptor.context().entrySet().stream())
                                .forEach(e -> put(e.getKey(), e.getValue()));
                    }
                };
            }

            {
                put("@type", "vocals:StreamDescriptor");
                put("dcat:dataset", stream);
            }
        };
    }

    @Override
    public String iri() {
        return id;
    }

}
