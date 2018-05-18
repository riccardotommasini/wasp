package it.polimi.rsp.server;

import it.polimi.rsp.Endpoint;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;

import java.lang.reflect.Method;

import static spark.Spark.post;

@Log
public class PostRequestHandler extends AbstractRequestHandler {

    public PostRequestHandler(Object object, Endpoint endpoint, Method method) {
        super(object, endpoint, method);
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
        log.info("Endpoint POST: [" + endpoint.uri + "] Ready");
        post(endpoint.uri, this);
    }
}
