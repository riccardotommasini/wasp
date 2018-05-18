package it.polimi.rsp.server.handlers;

import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.server.model.Answer;
import spark.Request;
import spark.Response;

import java.lang.reflect.Method;

import static spark.Spark.delete;
import static spark.Spark.put;

public class DeleteRequestHandler extends AbstractRequestHandler {

    public DeleteRequestHandler(Object object, Endpoint endpoint, Method method) {
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
        delete(endpoint.uri, this);
    }
}
