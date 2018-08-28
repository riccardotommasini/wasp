package it.polimi.sr.wasp.model;

import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.concept.tasks.AsynchTask;
import it.polimi.sr.wasp.server.model.concept.tasks.SynchTask;
import it.polimi.sr.wasp.server.model.concept.tasks.Task;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import lombok.extern.java.Log;

import java.util.Observable;

@Log
public class TestStream extends Observable implements Channel {

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
    public Channel add(Sink s) {
        return this;
    }

    @Override
    public Channel add(Channel c) {
        return this;
    }

    @Override
    public Channel add(Task t) {
        return this;
    }

    @Override
    public Channel add(AsynchTask t) {
        return this;
    }

    @Override
    public Channel add(SynchTask t) {
        return this;
    }

    @Override
    public Descriptor describe() {
        return null;
    }


    @Override
    public Channel put(String m) {
        return this;
    }
}
