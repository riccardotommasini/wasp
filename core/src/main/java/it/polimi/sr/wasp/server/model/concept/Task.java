package it.polimi.sr.wasp.server.model.concept;

public interface Task {

    String iri();

    Channel out();

    Channel[] in();

}
