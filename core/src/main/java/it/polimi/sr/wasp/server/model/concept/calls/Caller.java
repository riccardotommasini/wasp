package it.polimi.sr.wasp.server.model.concept.calls;

public interface Caller {

    void callback(Callee c, String m);
}
