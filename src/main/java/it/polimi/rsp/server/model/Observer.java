package it.polimi.rsp.server.model;

public interface Observer {

    void update(Object observed, Item msg);

}
