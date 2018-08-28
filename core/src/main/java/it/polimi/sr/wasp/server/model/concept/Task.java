package it.polimi.sr.wasp.server.model.concept;

import it.polimi.sr.wasp.server.model.concept.calls.AsynchCallee;
import it.polimi.sr.wasp.server.model.concept.calls.Callee;

public interface Task extends AsynchCallee, Callee {

    String iri();

    Channel out();

    Channel[] in();

}
