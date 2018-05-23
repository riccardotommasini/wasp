package it.polimi.sr.wasp.server.handlers;

import spark.Route;

public interface RequestHandler extends Route {

    void call();

}
