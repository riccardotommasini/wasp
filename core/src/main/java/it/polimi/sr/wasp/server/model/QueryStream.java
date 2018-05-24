package it.polimi.sr.wasp.server.model;

import it.polimi.sr.wasp.server.web.SourceTask;
import it.polimi.sr.wasp.server.web.Task;

import java.util.Observer;

public class QueryStream implements Stream {

    private final SourceTask task;

    public QueryStream(String s1, SourceTask q1) {
        this.task = q1;
    }

    @Override
    public String iri() {
        return null;
    }

    @Override
    public void message(String message) {
        this.task.update(message);
    }

    @Override
    public void observer(Observer observer) {

    }
}
