package it.polimi.sr.wasp.server.model.concept.calls;

public interface Callee {

    void await(Caller c, String m);
}
