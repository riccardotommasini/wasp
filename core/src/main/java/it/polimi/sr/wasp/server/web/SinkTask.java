package it.polimi.sr.wasp.server.web;

public interface SinkTask extends Task {

    void sink(Sink sink);

    Sink sink();
}
