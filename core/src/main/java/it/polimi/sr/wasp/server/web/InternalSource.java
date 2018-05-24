package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.server.model.Stream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InternalSource implements Proxy {

    private final Stream stream;


    @Override
    public void task(SourceTask t) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void message(Object msg) {

    }

    @Override
    public Stream stream() {
        return stream;
    }
}
