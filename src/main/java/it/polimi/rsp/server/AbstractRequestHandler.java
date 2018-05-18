package it.polimi.rsp.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.rsp.Endpoint;
import lombok.RequiredArgsConstructor;
import spark.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public abstract class AbstractRequestHandler implements RequestHandler {

    protected static final Gson gson = new Gson();
    protected static final int HTTP_BAD_REQUEST = 400;

    protected final Object engine;
    protected final Endpoint endpoint;
    protected Method method;

    public AbstractRequestHandler(Object engine, Endpoint endpoint, Method method) {
        this(engine, endpoint);
        this.method = method;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Endpoint.Par p : endpoint.params) {
            p.type = parameterTypes[p.index];
        }

    }

    protected Object[] getParams(Request r) {

        Object[] argvs = new Object[endpoint.params.length];
        JsonObject body = gson.fromJson(r.body(), JsonObject.class);

        for (Endpoint.Par param : endpoint.params) {
            if (param.uri)
                argvs[param.index] = r.params(param.name);
            else {
                if (body.has(param.name)) {
                    JsonElement json = body.get(param.name);
                    argvs[param.index] = gson.fromJson(json, param.type);
                } else
                    argvs[param.index] = gson.fromJson(r.body(), param.type);

            }
        }
        return argvs;
    }

    @Override
    public Answer process(Object engine, Object... params) {
        try {
            //TODO set up a return method that is actually meaninful
            return new Answer(200, method.invoke(engine, params));
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

}
