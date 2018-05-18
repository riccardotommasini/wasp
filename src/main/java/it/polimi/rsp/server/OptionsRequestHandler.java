package it.polimi.rsp.server;

import it.polimi.rsp.Endpoint;
import lombok.extern.java.Log;
import spark.Request;
import spark.Response;

import java.lang.reflect.Method;

import static spark.Spark.options;

@Log
public class OptionsRequestHandler extends AbstractRequestHandler {

    public OptionsRequestHandler(Object object, Endpoint endpoint, Method method) {
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
        log.info("Endpoint OPTIONS: [" + endpoint.uri + "] Ready");
        options(endpoint.uri, this);
    }
}
