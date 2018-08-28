package it.polimi.sr.wasp.server.model.concept;

import it.polimi.sr.wasp.server.model.description.Descriptor;

//Synchronous Sink
public interface Sink {

    Descriptor describe();

    void await(String m);


}
