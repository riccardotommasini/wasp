package it.polimi.sr.wasp.rsp.model;

import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.server.web.Proxy;
import it.polimi.sr.wasp.server.web.ProxyTask;
import it.polimi.sr.wasp.server.web.Sink;
import it.polimi.sr.wasp.server.web.Source;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Observable;

@NoArgsConstructor
@AllArgsConstructor
public class Query extends Observable implements ProxyTask {

    public String id;
    public String body;
    public Stream out_stream;
    public List<Stream> streams;

    @Override
    public String iri() {
        return id;
    }

    @Override
    public void update(String message) {

    }

    @Override
    public void sink(Sink sink) {

    }

    @Override
    public Sink sink() {
        return null;
    }

    @Override
    public String toString() {
        return "{\"id\":" + "\"" + id + "\",\"body\":\"" + body + "\"}";
    }

    @Override
    public Stream stream() {
        return null;
    }

    @Override
    public Proxy proxy() {
        return null;
    }

    @Override
    public void source(Source source) {
    }

    @Override
    public Source source() {
        return null;
    }
}
