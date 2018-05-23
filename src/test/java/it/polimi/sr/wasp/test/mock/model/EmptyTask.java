package it.polimi.sr.wasp.test.mock.model;

import it.polimi.sr.wasp.server.web.Sink;
import it.polimi.sr.wasp.server.web.Task;

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
