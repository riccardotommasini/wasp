package it.polimi.rsp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.rsp.vocals.annotations.Feature;
import it.polimi.rsp.vocals.annotations.FeatureObject;
import it.polimi.rsp.vocals.annotations.VocalsFactory;
import it.polimi.rsp.utils.Config;
import lombok.extern.java.Log;
import org.apache.jena.rdf.model.Model;
import org.reflections.Reflections;
import spark.Route;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.*;

@Log
public class Server {
    static final Gson gson = new Gson();
    static final Reflections reflections = new Reflections("it.polimi.rsp.mock");

    public void start(SREngine e, String engine_prop) {
        Config.initialize(engine_prop);
        int port = Config.getInstance().getServerPort();
        String host = Config.getInstance().getHostName();
        String name = Config.getInstance().getServerName();
        String base = "http://" + host + ":" + port + "/" + name;
        init(e, new ServiceSpec(VocalsFactory.toVocals(e.getClass(), name, base), name, host, port));
    }

    private void init(SREngine engine, ServiceSpec e) {
        port(e.getPort());
        path(e.getName(), () -> {
            get("", (req, res) -> getDescription(e.getModel()));
            e.getEndpoints().forEach(endpoint -> {
                        Arrays.stream(e.getClass().getInterfaces())
                                .filter(i -> i.isAnnotationPresent(Feature.class))
                                .filter(i -> i.getAnnotation(Feature.class).vocals().equals(endpoint.name))
                                .findFirst()
                                .ifPresent((Class<?> c) -> {
                                            Optional<Route> route;
                                            Method method = c.getMethods()[0];
                                            if (c.isAnnotationPresent(FeatureObject.class)) {
                                                if (Map.class.equals(c.getAnnotation(FeatureObject.class))) {
                                                    route = Optional.of((request, response) -> {
                                                        Map<String, String> map = new HashMap<>();
                                                        JsonObject body = gson.fromJson(request.body(), JsonObject.class);

                                                        for (Endpoint.Par param : endpoint.params) {
                                                            if (param.uri)
                                                                map.put(param.name, request.params(param.name));
                                                            else
                                                                map.put(param.name, body.get(param.name).getAsString());
                                                        }

                                                        Object ret = method.invoke(engine, map);
                                                        response.status(200);
                                                        if (ret != null) {
                                                            return ret.toString();
                                                        }
                                                        return "ok";
                                                    });
                                                } else {
                                                    Class<?> skeleton = c.getAnnotation(FeatureObject.class).skeleton();
                                                    if (!skeleton.isInterface()) {
                                                        route = Optional.of((request, response) -> {
                                                            JsonObject body = new Gson().fromJson(request.body(), JsonObject.class);

                                                            Object[] argvs = new Object[endpoint.params.length + 1];

                                                            for (Endpoint.Par param : endpoint.params) {
                                                                if (param.uri)
                                                                    argvs[param.index] = request.params(param.name);
                                                                else
                                                                    argvs[param.index] = gson.fromJson(body, skeleton);
                                                            }

                                                            Object ret = method.invoke(engine, argvs);
                                                            if (ret != null) {
                                                                log.info(ret.toString());
                                                                return ret.toString();
                                                            }
                                                            return "ok";
                                                        });
                                                    } else {
                                                        route = reflections.getSubTypesOf(skeleton).stream().findFirst().map(cc -> (request, response) -> {
                                                            try {
                                                                Object ret = method.invoke(engine, gson.fromJson(getJsonClass(endpoint, request.body()), cc));
                                                                response.status(200);
                                                                if (ret != null) {
                                                                    return ret.toString();
                                                                }

                                                                return "ok";
                                                            } catch (IllegalAccessException | InvocationTargetException e1) {
                                                                e1.printStackTrace();
                                                                response.status(500);
                                                                return response;
                                                            }
                                                        });
                                                    }
                                                }
                                            } else {
                                                route = Optional.of((request, response) -> {
                                                    Object ret = method.invoke(engine, request.params().values().toArray());
                                                    response.status(200);
                                                    if (ret != null) {
                                                        return ret.toString();
                                                    }
                                                    return "ok";
                                                });
                                            }
                                            route.ifPresent(r -> {
                                                log.info(endpoint.method + " " + endpoint.uri);
                                                switch (endpoint.method) {
                                                    case "GET":
                                                        get(endpoint.uri, r);
                                                        break;
                                                    case "POST":
                                                        post(endpoint.uri, r);
                                                        break;
                                                    case "PUT":
                                                        put(endpoint.uri, r);
                                                        break;
                                                    case "DELETE":
                                                        delete(endpoint.uri, r);
                                                        break;
                                                    case "OPTIONS":
                                                    default:
                                                        get(endpoint.uri, (req, res) -> getDescription(e.getModel()));
                                                }
                                            });
                                        }
                                );
                    }
            );
        });
        log.info("it.polimi.rsp.Server Running on port 8182");
    }

    private static String getDescription(Model model) {
        StringWriter writer = new StringWriter();
        model.write(writer, "JSON-LD");
        return writer.toString();
    }

    private static String getJsonClass(Endpoint endpoint, String r) {
        JsonObject body = new Gson().fromJson(r, JsonObject.class);

        return body.toString();
    }

}

