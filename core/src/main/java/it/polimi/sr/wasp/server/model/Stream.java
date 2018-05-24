package it.polimi.sr.wasp.server.model;

import java.util.Observer;

public interface Stream {

    String iri();

    void message(String task);

    void observer(Observer observer);
}
