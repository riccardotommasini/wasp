package it.polimi.rsp.server.web;

public interface Sink {

    void message(Object msg);

    void task(Task t);
}
