package it.polimi.rsp.server.model;

import it.polimi.rsp.server.web.Task;

import java.util.Observer;

public class QueryStream implements Stream {

    private final Task task;

    public QueryStream(String s1, Task q1) {
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
