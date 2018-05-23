package it.polimi.rsp;

import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.server.web.Sink;
import it.polimi.rsp.server.web.Task;

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
