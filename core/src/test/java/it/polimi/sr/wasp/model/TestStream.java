package it.polimi.sr.wasp.model;

import it.polimi.sr.wasp.server.model.concept.*;
import lombok.extern.java.Log;

import java.util.Observable;

@Log
public class TestStream extends Observable implements Channel, Named {

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
    public void yeild(String m) {

    }

    @Override
    public void await(Source s, String m) {

    }

    @Override
    public void add(Sink s) {

    }

    @Override
    public Channel add(Channel c) {
        return null;
    }

    @Override
    public Channel apply(Task t) {
        return null;
    }
}
