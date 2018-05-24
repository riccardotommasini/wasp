package it.polimi.sr.wasp.rsp.model;

import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.server.web.Task;

public class EmptyTask implements Task {

    @Override
    public String iri() {
        return null;
    }


    @Override
    public Stream stream() {
        return null;
    }

}
