package it.polimi.sr.wasp.server.handlers;

import it.polimi.sr.wasp.server.model.Endpoint;
import it.polimi.sr.wasp.server.model.KeyFactory;
import it.polimi.sr.wasp.server.model.StatusManager;
import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.server.web.Proxy;
import it.polimi.sr.wasp.server.web.Task;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.model.Deletable;
import it.polimi.sr.wasp.vocals.annotations.model.Exposed;
import it.polimi.sr.wasp.server.model.Key;
import lombok.extern.java.Log;
import spark.Request;
import spark.Response;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static spark.Spark.delete;
import static spark.Spark.get;

@Log
public class RequestHandlerFactory {

    public static Optional<RequestHandler> getServices(Object engine, Endpoint endpoint) {
        return Arrays.stream(engine.getClass().getInterfaces())
                .filter(i -> i.isAnnotationPresent(Feature.class))
                .filter(i -> i.getAnnotation(Feature.class).name().equals(endpoint.name))
                .findFirst()
                .map(aClass -> aClass.getMethods()[0]).map(method -> {
                    switch (endpoint.method) {
                        case POST:
                            return new AbstractReflectiveRequestHandler.PostRequestHandler(engine, endpoint, method);
                        case PUT:
                            return new AbstractReflectiveRequestHandler.PutRequestHandler(engine, endpoint, method);
                        case DELETE:
                            return new AbstractReflectiveRequestHandler.DeleteRequestHandler(engine, endpoint, method);
                        case OPTIONS:
                            return new AbstractReflectiveRequestHandler.OptionsRequestHandler(engine, endpoint, method);
                        case GET:
                        default:
                            return new AbstractReflectiveRequestHandler.GetRequestHandler(engine, endpoint, method);
                    }
                });
    }

    public static Optional<RequestHandler> getGetters(Object engine, Endpoint endpoint) {
        java.util.stream.Stream<? extends Class<?>> classStream = Arrays.stream(engine.getClass().getInterfaces())
                .filter(i -> i.isAnnotationPresent(Feature.class))
                .map(aClass -> aClass.getMethods()[0])
                .map(Method::getReturnType)
                .filter(i -> i.isAnnotationPresent(Exposed.class))
                .filter(t -> t.getAnnotation(Exposed.class).name().equals(endpoint.name));

        if (endpoint.params.length == 0) {
            java.util.stream.Stream<RequestHandler> requestHandlerStream = classStream
                    .map(t -> new RequestHandler() {
                        @Override
                        public void call() {
                            log.info("Endpoint GET: [" + endpoint.uri + "] Ready");
                            get(endpoint.uri, this);
                        }

                        @Override
                        public Object handle(Request request, Response response) throws Exception {
                            return getList(t);
                        }
                    });
            return requestHandlerStream.findAny();
        } else {
            java.util.stream.Stream<RequestHandler> requestHandlerStream = classStream
                    .map(t -> new RequestHandler() {
                        @Override
                        public void call() {
                            log.info("Endpoint GET: [" + endpoint.uri + "] Ready");
                            get(endpoint.uri, this);
                        }

                        @Override
                        public Object handle(Request request, Response response) throws Exception {
                            if (getObject(KeyFactory.create(request.params(endpoint.params[0].name)), t).isPresent())
                                return getObject(KeyFactory.create(request.params(endpoint.params[0].name)), t).get();
                            else return null;
                        }
                    });
            return requestHandlerStream.findAny();
        }
    }

    public static Optional<RequestHandler> getDeleters(Object engine, Endpoint endpoint) {
        java.util.stream.Stream<? extends Class<?>> classStream = Arrays.stream(engine.getClass().getInterfaces())
                .filter(i -> i.isAnnotationPresent(Feature.class))
                .map(aClass -> aClass.getMethods()[0])
                .map(Method::getReturnType)
                .filter(t -> t.isAnnotationPresent(Deletable.class))
                .filter(t -> t.getAnnotation(Deletable.class).name().equals(endpoint.name));

        if (endpoint.params.length == 0) {
            java.util.stream.Stream<RequestHandler> requestHandlerStream = classStream
                    .map(t -> new RequestHandler() {
                        @Override
                        public void call() {
                            log.info("Endpoint DELETE: [" + endpoint.uri + "] Ready");
                            delete(endpoint.uri, this);
                        }

                        @Override
                        public Object handle(Request request, Response response) throws Exception {
                            return deleteList(t);
                        }
                    });
            return requestHandlerStream.findAny();
        } else {
            java.util.stream.Stream requestHandlerStream = classStream
                    .map(t -> new RequestHandler() {
                        @Override
                        public void call() {
                            log.info("Endpoint DELETE: [" + endpoint.uri + "] Ready");
                            delete(endpoint.uri, this);
                        }

                        @Override
                        public Object handle(Request request, Response response) throws Exception {

                            Endpoint.Par param = endpoint.params[0];
                            return deleteObject(KeyFactory.create(request.params(param.name)), t);
                        }
                    });
            return requestHandlerStream.findAny();
        }

    }

    private static Collection<?> getList(Class<?> t) {
        if (Stream.class.isAssignableFrom(t)) {
            return StatusManager.streams.values();
        } else if (Task.class.isAssignableFrom(t)) {
            return StatusManager.tasks.values();
        } else if (Proxy.class.isAssignableFrom(t)) {
            return StatusManager.proxies.values();
        } else return Collections.emptyList();
    }

    private static <T> Optional<T> getObject(Key key, Class<?> t) {
        if (Stream.class.isAssignableFrom(t)) {
            return (Optional<T>) StatusManager.getStream(key);
        } else if (Task.class.isAssignableFrom(t)) {
            return (Optional<T>) StatusManager.getTask(key);
        } else if (Proxy.class.isAssignableFrom(t)) {
            return (Optional<T>) StatusManager.getTask(key);
        }
        return Optional.empty();
    }

    private static Object deleteList(Class<?> t) {
        if (Stream.class.isAssignableFrom(t)) {
            StatusManager.streams.clear();
        } else if (Task.class.isAssignableFrom(t)) {
            StatusManager.tasks.clear();
        } else if (Proxy.class.isAssignableFrom(t)) {
            StatusManager.proxies.clear();
        }
        return null;
    }

    private static Object deleteObject(Key key, Class<?> t) {
        if (Stream.class.isAssignableFrom(t)) {
            return StatusManager.streams.remove(key);
        } else if (Task.class.isAssignableFrom(t)) {
            return StatusManager.tasks.remove(key);
        } else if (Proxy.class.isAssignableFrom(t)) {
            return StatusManager.proxies.remove(key);
        }
        return null;
    }
}