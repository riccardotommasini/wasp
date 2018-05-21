package it.polimi.rsp.server.handlers;

import spark.Route;

public interface RequestHandler extends Route {

    void call();

}
