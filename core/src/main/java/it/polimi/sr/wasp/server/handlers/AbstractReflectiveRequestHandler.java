package it.polimi.sr.wasp.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyException;

import static it.polimi.sr.wasp.server.MyService.*;

@RequiredArgsConstructor
public abstract class AbstractReflectiveRequestHandler implements RequestHandler, Route {

    protected static final Gson gson = new Gson();
    protected static final int HTTP_BAD_REQUEST = 400;

    protected final Object engine;
    protected final Endpoint endpoint;
    protected Method method;
    protected int param_num;

    public AbstractReflectiveRequestHandler(Object engine, Endpoint endpoint, Method method) {
        this(engine, endpoint);
        this.method = method;
        Class<?>[] parameterTypes = method.getParameterTypes();
        this.param_num = parameterTypes.length;
        for (Endpoint.Par p : endpoint.params) {
            p.type = parameterTypes[p.index];
        }
    }

    protected Object[] getParams(Request r) {

        Object[] argvs = new Object[param_num];

        for (int i = 0; i < param_num; i++) {
            Endpoint.Par param = endpoint.params[i];
            if (param.uri)
                argvs[param.index] = r.params(param.name);
            else if (param.type != null && (param.type.isPrimitive() || String.class.equals(param.type))) {
                String body1 = r.body();
                argvs[param.index] = param.type.cast(gson.fromJson(body1, JsonObject.class).get(param.name).getAsString());
            } else {
                String body1 = r.body();
                argvs[param.index] = gson.fromJson(body1, param.type);
            }
        }
        return argvs;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Answer answer = process(engine, getParams(request));
        response.status(answer.getCode());
        response.type(ContentType.TEXT_PLAIN.getMimeType());
        return answer;
    }

    public Answer process(Object engine, Object... params) {
        try {
            //TODO set up a return method that is actually meaningful
            Object invoke = method.invoke(engine, params);
            return new Answer(200, invoke);
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            Throwable cause = e.getCause().getCause();
            if (cause instanceof DuplicateException)
                return new Answer(409, cause.getMessage());
            else if (cause instanceof ResourceNotFound || cause instanceof KeyException)
                return new Answer(404, cause.getMessage());
            else
                return new Answer(HTTP_BAD_REQUEST, cause.getMessage());
        }
    }


    @Log
    public static class GetRequestHandler extends AbstractReflectiveRequestHandler {

        public GetRequestHandler(Object object, Endpoint endpoint, Method method) {
            super(object, endpoint, method);
        }

        public GetRequestHandler(Object object, Endpoint endpoint) {
            super(object, endpoint);
        }

        @Override
        public void call() {
            log.info("Endpoint GET: [" + endpoint.uri + "] Ready");
            get(endpoint.uri, this);
        }
    }

    @Log
    public static class PostRequestHandler extends AbstractReflectiveRequestHandler {

        public PostRequestHandler(Object object, Endpoint endpoint, Method method) {
            super(object, endpoint, method);
        }

        @Override
        public void call() {
            log.info("Endpoint POST: [" + endpoint.uri + "] Ready");
            post(endpoint.uri, this);
        }
    }


    @Log
    public static class PutRequestHandler extends AbstractReflectiveRequestHandler {

        public PutRequestHandler(Object object, Endpoint endpoint, Method method) {
            super(object, endpoint, method);
        }

        @Override
        public void call() {
            log.info("Endpoint PUT: [" + endpoint.uri + "] Ready");
            put(endpoint.uri, this);
        }
    }


    @Log
    public static class OptionsRequestHandler extends AbstractReflectiveRequestHandler {

        public OptionsRequestHandler(Object object, Endpoint endpoint, Method method) {
            super(object, endpoint, method);
        }

        @Override
        public void call() {
            log.info("Endpoint OPTIONS: [" + endpoint.uri + "] Ready");
            options(endpoint.uri, this);
        }
    }


    @Log
    public static class DeleteRequestHandler extends AbstractReflectiveRequestHandler {

        public DeleteRequestHandler(Object object, Endpoint endpoint, Method method) {
            super(object, endpoint, method);
        }

        @Override
        public void call() {
            log.info("Endpoint DELETE: [" + endpoint.uri + "] Ready");
            delete(endpoint.uri, this);
        }
    }


}
