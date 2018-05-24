package it.polimi.sr.wasp.server.handlers.std;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.VocalsStub;
import it.polimi.sr.wasp.server.handlers.AbstractReflectiveRequestHandler;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;

import java.io.UnsupportedEncodingException;

import static spark.Spark.get;

@Log
public class SGraphRequestHandler extends AbstractReflectiveRequestHandler {

    private VocalsStub model;

    public SGraphRequestHandler(VocalsStub m) {
        super(null, new Endpoint("sgraph", "", HttpMethod.GET, "sgraph", new Endpoint.Par[]{}));
        this.model = m;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type(ContentType.APPLICATION_JSON.getMimeType());
        return getDescription(model);
    }

    @Override
    public void call() {
        log.info("SGRAPH Endpoint GET: [" + endpoint.uri + "] Ready");
        get(endpoint.uri, ContentType.APPLICATION_JSON.getMimeType(), this);
    }

    private String getDescription(VocalsStub model) throws UnsupportedEncodingException {
        return model.toJsonLD();
    }
}
