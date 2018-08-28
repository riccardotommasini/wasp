package it.polimi.sr.wasp.server.model.concept;

import it.polimi.sr.wasp.server.model.concept.calls.AsynchCallee;
import it.polimi.sr.wasp.server.model.description.Descriptor;

public interface Sink extends AsynchCallee {

    void yield(String m);

    Descriptor describe();

}
