package it.polimi.sr.wasp.rsp.model;

import it.polimi.sr.wasp.server.model.concept.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Stream implements Channel, Named {

    public String id;
    public String source;

    protected List<Sink> sinks = new ArrayList<>();
    protected List<Task> tasks = new ArrayList<>();

    public Stream(String id, String uri) {
        this.id = id;
        this.source = uri;
    }

    @Override
    public String toString() {
        return "{\"stream\": \"" + id + "\", \"source\": \"" + source + "\"}";
    }


    @Override
    public void yeild(String task) {
        log.debug(" Yield message " + task);
        sinks.forEach(sink -> sink.await(this, task));
    }

    @Override
    public void await(Source s, String m) {
        log.debug(id + " got a message " + m + "from " + s);
        yeild(m);
    }

    @Override
    public void add(Sink observer) {
        sinks.add(observer);
    }

    @Override
    public Channel add(Channel c) {
        //TODO
        return c;
    }

    @Override
    public Channel apply(Task t) {
        tasks.add(t);
        return this;
    }

    @Override
    public String iri() {
        return source;
    }
}
