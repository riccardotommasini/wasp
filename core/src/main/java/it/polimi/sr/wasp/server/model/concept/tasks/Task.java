package it.polimi.sr.wasp.server.model.concept.tasks;

import it.polimi.sr.wasp.server.model.concept.Channel;

public interface Task {

    String iri();

    Channel out();

    Channel[] in();

}
