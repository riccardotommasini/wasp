package it.polimi.rsp.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.rsp.server.model.Answer;
import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.server.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static spark.Spark.*;

@RequiredArgsConstructor
public abstract class AbstractRequestHandler implements RequestHandler {

    protected static final Gson gson = new Gson();
    protected static final int HTTP_BAD_REQUEST = 400;

    protected final Object engine;
    protected final Endpoint endpoint;
    protected Method method;
    protected int param_num;

    public AbstractRequestHandler(Object engine, Endpoint endpoint, Method method) {
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

        for (Endpoint.Par param : endpoint.params) {
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
        response.type(ContentType.APPLICATION_JSON.getMimeType());
        return answer;
    }

    public Answer process(Object engine, Object... params) {
        try {
            //TODO set up a return method that is actually meaningful
            Object invoke = method.invoke(engine, params);

            Status.add(invoke);
            return new Answer(200, invoke);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new Answer(HTTP_BAD_REQUEST, e.getCause());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return new Answer(HTTP_BAD_REQUEST, e.getCause());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new Answer(HTTP_BAD_REQUEST, e.getCause());
        }
    }


    @Log
    public static class GetRequestHandler extends AbstractRequestHandler {

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
    public static class PostRequestHandler extends AbstractRequestHandler {

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
    public static class PutRequestHandler extends AbstractRequestHandler {

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
    public static class OptionsRequestHandler extends AbstractRequestHandler {

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
    public static class DeleteRequestHandler extends AbstractRequestHandler {

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
