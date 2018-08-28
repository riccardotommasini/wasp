package it.polimi.sr.wasp.server.model.concept;

import it.polimi.sr.wasp.server.model.concept.tasks.AsynchTask;
import it.polimi.sr.wasp.server.model.concept.tasks.SynchTask;
import it.polimi.sr.wasp.server.model.concept.tasks.Task;
import it.polimi.sr.wasp.server.model.description.Descriptor;

//Synchronous Sink
public interface Channel {

    String iri();

    Channel put(String m);

    Channel add(Sink s);

    Channel add(Channel c);

    Channel add(Task t);

    Channel add(AsynchTask t);

    Channel add(SynchTask t);

    Descriptor describe();
}