package it.polimi.sr.wasp.server.model.concept;

public interface Channel extends Named{

    void yeild(String m);

    void await(Source s, String m);

    void add(Sink s);

    Channel add(Channel c);

    Channel apply(Task t);
}
