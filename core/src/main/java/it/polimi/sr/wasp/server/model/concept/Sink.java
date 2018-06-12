package it.polimi.sr.wasp.server.model.concept;

public interface Sink {

    void await(Source s, String m);

    void await(Channel c, String m);

    Descriptor describe();

}
