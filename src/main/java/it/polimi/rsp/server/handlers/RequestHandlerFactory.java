package it.polimi.rsp.server.handlers;

import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.vocals.annotations.features.Feature;

import java.util.Arrays;
import java.util.Optional;

public class RequestHandlerFactory {

    public static Optional<RequestHandler> getHandler(Object engine, Endpoint endpoint) {
        return Arrays.stream(engine.getClass().getInterfaces())
                .filter(i -> i.isAnnotationPresent(Feature.class))
                .filter(i -> i.getAnnotation(Feature.class).name().equals(endpoint.name))
                .findFirst()
                .map(aClass -> aClass.getMethods()[0]).map(method -> {
                    switch (endpoint.method) {
                        case POST:
                            return new AbstractRequestHandler.PostRequestHandler(engine, endpoint, method);
                        case PUT:
                            return new AbstractRequestHandler.PutRequestHandler(engine, endpoint, method);
                        case DELETE:
                            return new AbstractRequestHandler.DeleteRequestHandler(engine, endpoint, method);
                        case OPTIONS:
                            return new AbstractRequestHandler.OptionsRequestHandler(engine, endpoint, method);
                        case GET:
                        default:
                            return new AbstractRequestHandler.GetRequestHandler(engine, endpoint, method);
                    }
                });

    }
}