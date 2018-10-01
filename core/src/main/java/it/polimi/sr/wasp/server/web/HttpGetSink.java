package it.polimi.sr.wasp.server.web;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.sr.wasp.VOCABS;
import it.polimi.sr.wasp.server.handlers.AbstractReflectiveRequestHandler;
import it.polimi.sr.wasp.server.handlers.Answer;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import it.polimi.sr.wasp.server.model.description.DescriptorHashMap;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.LinkedHashMap;
import java.util.Map;


@Log
public class HttpGetSink extends AbstractReflectiveRequestHandler implements Sink {

    private final int max_number;
    private final Service http;
    private final LinkedHashMap<String, String> messages;
    public final String base;

    public HttpGetSink(String base, String stream, String oid, int messages, Service ws) {
        super(null, new Endpoint(stream, oid, HttpMethod.GET, "HttpSink", new Endpoint.Par[]{}));
        this.http = ws;
        this.base = base;
        this.max_number = messages;
        this.messages = new LinkedHashMap<String, String>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return this.size() > max_number;
            }
        };

    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Answer answer = new Answer(200, messages.values());
        response.status(answer.getCode());
        response.type(ContentType.APPLICATION_JSON.getMimeType());
        return answer;
    }

    @Override
    public void call() {
        log.info("[" + endpoint.name + "] Endpoint GET: [" + endpoint.uri + "] Ready On ");
        http.get(endpoint.uri, ContentType.APPLICATION_JSON.getMimeType(), this);
        //http.get(URIUtils.addParam(endpoint.uri, "window"), ContentType.APPLICATION_JSON.getMimeType(), this);
    }

    @Override
    public void await(String m) {
        messages.put(System.currentTimeMillis() + "", m);
    }


    @Override
    public Descriptor describe() {
        LinkedHashMap<String, Object> context = new LinkedHashMap<>();
        context.put(VOCABS.DCAT.prefix, VOCABS.DCAT.uri);
        context.put(VOCABS.VOCALS.prefix, VOCABS.VOCALS.uri);

        return new DescriptorHashMap() {

            @Override
            public Map<String, Object> context() {
                return context;
            }

            {
                put("@type", "vocals:StreamEndpoint");
                put("dcat:accessURL", HttpGetSink.this.base + endpoint.uri);
            }
        };
    }
}
