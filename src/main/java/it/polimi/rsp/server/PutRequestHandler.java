package it.polimi.rsp.server;

import it.polimi.rsp.Endpoint;
import lombok.extern.java.Log;
import spark.Request;
import spark.Response;

import java.lang.reflect.Method;

import static spark.Spark.put;

@Log
public class PutRequestHandler extends AbstractRequestHandler {

    public PutRequestHandler(Object object, Endpoint endpoint, Method method) {
        super(object, endpoint, method);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Answer answer = process(engine, getParams(request));
        response.status(answer.getCode());
        response.body(answer.getBody().toString());
        return response;
    }

    @Override
    public void call() {
        log.info("Endpoint PUT: [" + endpoint.uri + "] Ready");
        put(endpoint.uri, this);
    }
}
