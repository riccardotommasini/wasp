package it.polimi.sr.wasp.rsp.server;

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
public class ESDRequestHandler extends AbstractReflectiveRequestHandler {

    private final String name;
    private VocalsStub model;

    public ESDRequestHandler(VocalsStub m, String name) {
        super(null, new Endpoint("esd", "", HttpMethod.GET, "esd", new Endpoint.Par[]{}));
        this.name = name;
        this.model = m;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type(ContentType.APPLICATION_JSON.getMimeType());
        return getDescription(model).replace("localhost", name);
    }

    @Override
    public void call() {
        log.info("ESD Endpoint GET: [" + endpoint.uri + "] Ready");
        get(endpoint.uri, ContentType.APPLICATION_JSON.getMimeType(), this);
    }

    private String getDescription(VocalsStub model) throws UnsupportedEncodingException {
        return model.toJsonLD();
    }
}
