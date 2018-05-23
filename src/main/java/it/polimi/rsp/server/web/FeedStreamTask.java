package it.polimi.rsp.server.web;

import it.polimi.rsp.server.model.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
public class FeedStreamTask implements Task {

    private final Stream stream;

    @Override
    public String iri() {
        return "vocals:feedStreamTask";
    }

    @Override
    public void update(String message) {
        log.info(message);
        stream.message(message);
    }

    @Override
    public void sink(Sink sink) {

    }

}
