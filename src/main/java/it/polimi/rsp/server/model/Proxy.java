package it.polimi.rsp.server.model;

public interface Proxy extends Sink, Source {

    boolean isChanged();

    void addObserver(Observer observer);
}
