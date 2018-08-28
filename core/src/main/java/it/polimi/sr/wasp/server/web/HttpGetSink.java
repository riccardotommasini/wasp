package it.polimi.sr.wasp.server.web;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.sr.wasp.server.handlers.AbstractReflectiveRequestHandler;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import it.polimi.sr.wasp.server.model.description.DescriptorHashMap;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.HashMap;
import java.util.Map;


@Log
public class HttpGetSink extends AbstractReflectiveRequestHandler implements Sink {

    private final int pastWindows;
    private final Service http;
    private String message = "init";
    private Map<String, String> windows = new HashMap<>();

    public HttpGetSink(String stream, String uri, int pastWindows, Service ws) {
        super(null, new Endpoint(stream, uri, HttpMethod.GET, "esd", new Endpoint.Par[]{}));
        this.http = ws;
        this.pastWindows = pastWindows;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (!request.params().containsKey("window")) {
            return message;
        } else {
            return windows.get(request.params("window"));
        }
    }

    @Override
    public void call() {
        log.info("[" + endpoint.name + "] Endpoint GET: [" + endpoint.uri + "] Ready On ");
        http.get(endpoint.uri, ContentType.APPLICATION_JSON.getMimeType(), this);
        http.get(URIUtils.addParam(endpoint.uri, "window"), ContentType.APPLICATION_JSON.getMimeType(), this);
    }

    @Override
    public void await(String m) {
        //TODO new window blank node
        this.message = m;
    }


    @Override
    public Descriptor describe() {
        return new DescriptorHashMap() {
            {
                put("@type", "vocals:StreamEndpoint");
                put("dcat:accessURL", "http://");
                //TODO
            }
        };
    }
}
