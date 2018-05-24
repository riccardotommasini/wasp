package it.polimi.sr.wasp.server.web;

public interface SourceTask extends Task {

    void source(Source source);

    Source source();

    void update(String message);

}
