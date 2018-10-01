package it.polimi.sr.wasp.rsp.publisher;

import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.tasks.Task;
import lombok.Getter;

@Getter
public class StreamPublication implements Task {

    private String id;
    private Channel channel;

    public StreamPublication(Channel webChannel, String id) {
        this.id = id;
        this.channel = webChannel;
    }

    @Override
    public String iri() {
        return id;
    }

    @Override
    public Channel out() {
        return channel;
    }

    @Override
    public Channel[] in() {
        return new Channel[0];
    }

}
