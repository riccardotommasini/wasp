package it.polimi.sr.wasp.server.model.concept.tasks;

public interface SynchTask extends Task {
    void await(String m);

}
