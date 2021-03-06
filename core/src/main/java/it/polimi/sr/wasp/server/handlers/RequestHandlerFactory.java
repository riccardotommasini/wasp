package it.polimi.sr.wasp.server.handlers;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.model.Deletable;
import it.polimi.rsp.vocals.core.annotations.model.Exposed;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.tasks.Task;
import it.polimi.sr.wasp.server.model.persist.Key;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import lombok.extern.java.Log;
import spark.Request;
import spark.Response;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.delete;
import static spark.Spark.get;

@Log
public class RequestHandlerFactory {

    public static Optional<RequestHandler> getServices(Object engine, Endpoint endpoint) {

        java.util.stream.Stream<RequestHandler> abstractReflectiveRequestHandlerStream = getEngineClass(engine.getClass(), endpoint.name).stream()
                .map(method -> {
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
        return abstractReflectiveRequestHandlerStream.findFirst();
    }

    private static List<Method> getEngineClass(Class<?> engine, String name) {

        List<Method> collect = Arrays.stream(engine.getInterfaces())
                .filter(i -> i.isAnnotationPresent(Feature.class))
                .filter(i -> i.getAnnotation(Feature.class).name().equals(name))
                .map(i -> i.getMethods()[0]).collect(Collectors.toList());

        List<Method> collect1 = Arrays.stream(engine.getInterfaces()).flatMap(aClass -> Arrays.stream(aClass.getMethods()))
                .filter(m -> m.getAnnotation(Feature.class) != null)
                .filter(m -> m.getAnnotation(Feature.class).name().equals(name))
                .collect(Collectors.toList());

        collect.addAll(collect1);

        if (!Object.class.equals(engine))
            collect.addAll(getEngineClass(engine.getSuperclass(), name));
        return collect;

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
        if (Channel.class.isAssignableFrom(t)) {
            return StatusManager.channels.values();
        } else if (Task.class.isAssignableFrom(t)) {
            return StatusManager.tasks.values();
        } else return Collections.emptyList();
    }

    private static <T> Optional<T> getObject(Key key, Class<?> t) {
        if (Channel.class.isAssignableFrom(t)) {
            return (Optional<T>) StatusManager.getChannel(key);
        } else if (Task.class.isAssignableFrom(t)) {
            return (Optional<T>) StatusManager.getTask(key);
        }
        return Optional.empty();
    }

    private static Object deleteList(Class<?> t) {
        if (Channel.class.isAssignableFrom(t)) {
            StatusManager.channels.clear();
        } else if (Task.class.isAssignableFrom(t)) {
            StatusManager.tasks.clear();
        }
        return null;
    }

    private static Object deleteObject(Key key, Class<?> t) {
        if (Channel.class.isAssignableFrom(t)) {
            return StatusManager.channels.remove(key);
        } else if (Task.class.isAssignableFrom(t)) {
            return StatusManager.tasks.remove(key);
        }
        return null;
    }
}