package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.server.model.Stream;

import java.util.Observable;
import java.util.Observer;

public class FlushStreamTask implements Observer, Task {

    private final Stream stream;
    private Sink sink;

    public FlushStreamTask(Stream stream) {
        this.stream = stream;
        this.stream.observer(this);
    }

    @Override
    public String iri() {
        return "vocals:FlushStreamTask";
    }


    @Override
    public void update(String message) {
        sink.message(message);
    }

    @Override
    public void sink(Sink sink) {
        this.sink = sink;
    }

    @Override
    public void update(Observable o, Object arg) {
        update((String) arg);
    }
}
