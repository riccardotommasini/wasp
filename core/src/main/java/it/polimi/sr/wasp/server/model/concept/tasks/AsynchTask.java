package it.polimi.sr.wasp.server.model.concept.tasks;

import it.polimi.sr.wasp.server.model.concept.Channel;

public interface AsynchTask extends Task {

    void yield(Channel c, String m);


}
