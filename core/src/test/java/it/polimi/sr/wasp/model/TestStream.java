package it.polimi.sr.wasp.model;

import it.polimi.sr.wasp.server.model.Stream;
import lombok.extern.java.Log;

import java.util.Observable;
import java.util.Observer;

@Log
public class TestStream extends Observable implements Stream {

    public String id;
    public String uri;

    public TestStream(String id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "{\"stream\": \"" + id + "\", \"uri\": \"" + uri + "\"}";
    }


    @Override
    public String iri() {
        return uri;
    }

    @Override
    public void message(String msg) {
        log.info(id + " got a message " + msg);
        setChanged();
        notifyObservers(msg);
    }

    @Override
    public void observer(Observer observer) {
        addObserver(observer);
    }

}