package it.polimi.rsp.server;

import spark.Route;

public interface RequestHandler extends Route {

    Answer process(Object o, Object[] params);

    void call();

}
