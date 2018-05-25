package it.polimi.sr.wasp.rsp.model;

import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Named;
import it.polimi.sr.wasp.server.model.concept.Task;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

@AllArgsConstructor
@RequiredArgsConstructor
public class Query extends Observable implements Task, Named {

    public final String id;
    public final String body;

    protected Channel out;
    protected List<Channel> in = new ArrayList<>();


    @Override
    public Channel out() {
        return out;
    }

    @Override
    public Channel[] in() {
        return in.toArray(new Channel[in.size()]);
    }

    @Override
    public String iri() {
        return id;
    }

    public void add(Channel in){
        in.add(in);
    }
}
