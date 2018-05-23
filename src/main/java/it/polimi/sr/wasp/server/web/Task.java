package it.polimi.sr.wasp.server.web;

public interface Task {

    String iri();

    void update(String message);

    void sink(Sink sink);
}
