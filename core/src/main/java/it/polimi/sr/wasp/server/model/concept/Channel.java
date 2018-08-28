package it.polimi.sr.wasp.server.model.concept;

import it.polimi.sr.wasp.server.model.concept.calls.AsynchCallee;
import it.polimi.sr.wasp.server.model.concept.calls.Callee;
import it.polimi.sr.wasp.server.model.description.Descriptor;

public interface Channel extends AsynchCallee, Callee {

    String iri();

    void add(Sink s);

    Channel add(Channel c);

    Channel apply(Task t);

    Descriptor describe();
}
