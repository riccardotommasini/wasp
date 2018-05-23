package it.polimi.rsp.server.web;

import it.polimi.rsp.server.model.Stream;

public interface Proxy extends Sink, Source {

    Stream stream();

}