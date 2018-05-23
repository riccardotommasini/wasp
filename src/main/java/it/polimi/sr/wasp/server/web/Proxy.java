package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.server.model.Stream;

public interface Proxy extends Sink, Source {

    Stream stream();

}