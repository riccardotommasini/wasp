package it.polimi.rsp.server.handlers;

import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.server.model.Answer;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;

import java.lang.reflect.Method;

import static spark.Spark.get;

@Log
public class GetRequestHandler extends AbstractRequestHandler {

    public GetRequestHandler(Object object, Endpoint endpoint, Method method) {
        super(object, endpoint, method);
    }

    public GetRequestHandler(Object object, Endpoint endpoint) {
        super(object, endpoint);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Answer answer = process(engine, getParams(request));
        response.status(answer.getCode());
        response.type(ContentType.APPLICATION_JSON.getMimeType());
        return gson.toJson(answer.getBody());
    }

    @Override
    public void call() {
        log.info("Endpoint GET: [" + endpoint.uri + "] Ready");
        get(endpoint.uri, this);
    }
}
