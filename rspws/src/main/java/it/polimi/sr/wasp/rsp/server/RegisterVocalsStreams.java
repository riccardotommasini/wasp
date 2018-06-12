package it.polimi.sr.wasp.rsp.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.VocalsFactory;
import it.polimi.rsp.vocals.core.annotations.VocalsStub;
import it.polimi.sr.wasp.server.handlers.AbstractReflectiveRequestHandler;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;

import java.io.UnsupportedEncodingException;

import static spark.Spark.get;


@Log
public class RegisterVocalsStreams extends AbstractReflectiveRequestHandler {

    private final VocalsFactory factory;
    private VocalsStub model;
    private Gson gson;

    public RegisterVocalsStreams(VocalsStub m, VocalsFactory factory) {
        super(null, new Endpoint("channels", "channels", HttpMethod.POST, "vocals:registration", new Endpoint.Par[]{}));
        this.model = m;
        this.gson = new Gson();
        this.factory = factory;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        String uri = gson.fromJson(request.body(), JsonObject.class).get("uri").getAsString();

        response.type();
        return getDescription(model);
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
