package it.polimi.sr.wasp.server.web;

public interface Source {

    void task(SourceTask t);

    void stop();

}
