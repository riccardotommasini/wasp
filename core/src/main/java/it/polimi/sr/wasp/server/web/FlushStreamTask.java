package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.server.model.Stream;

import java.util.Observable;
import java.util.Observer;

public class FlushStreamTask implements Observer, SinkTask {

    private final Stream stream;
    private Sink sink;

    public FlushStreamTask(Stream stream, Sink sink) {
        this.stream = stream;
        this.sink = sink;
        this.stream.observer(this);
    }

    @Override
    public String iri() {
        return "vocals:FlushStreamTask";
    }

    @Override
    public Stream stream() {
        return stream;
    }

    @Override
    public void sink(Sink sink) {
        this.sink = sink;
    }

    @Override
    public Sink sink() {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        sink.message(arg);
    }
}
