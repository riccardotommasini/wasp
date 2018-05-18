package it.polimi.rsp.server.handlers;

import it.polimi.rsp.server.model.Answer;
import spark.Route;

public interface RequestHandler extends Route {

    Answer process(Object o, Object[] params);

    void call();

}
