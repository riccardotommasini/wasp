package it.polimi.rsp.server.web;

public interface Task {

    String iri();

    void update(String message);

    void sink(Sink sink);
}
