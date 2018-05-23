package it.polimi.rsp.test.mock;

import it.polimi.rsp.server.web.Sink;
import it.polimi.rsp.server.web.Task;

public class EmptyTask implements Task {

    public EmptyTask() {
    }

    @Override
    public String iri() {
        return null;
    }

    @Override
    public void update(String message) {

    }

    @Override
    public void sink(Sink sink) {

    }

}
