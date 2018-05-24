package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.server.model.Stream;
import lombok.extern.java.Log;

@Log
public class FeedStreamTask implements SourceTask {

    private final Stream stream;
    private final Source source;

    public FeedStreamTask(Stream stream, Source source) {
        this.stream = stream;
        this.source = source;
        this.source.task(this);
    }

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
    public Stream stream() {
        return stream;
    }

    @Override
    public void source(Source source) {

    }

    @Override
    public Source source() {
        return source;
    }
}
