package it.polimi.rsp.server.web;

import it.polimi.rsp.server.model.Stream;

public class SourceSinkFactory {

    public static Source webSocket(Stream o) {
        return new WebSocketSource(o);
    }

    public static Proxy internal(Stream o) {
        return new InternalSource(o);
    }
}
