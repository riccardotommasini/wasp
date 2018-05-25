package it.polimi.sr.wasp.rsp.model;

import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Task;

public class EmptyTask implements Task {

    @Override
    public Channel out() {
        return null;
    }

    @Override
    public Channel[] in() {
        return new Channel[0];
    }
}
