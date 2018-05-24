package it.polimi.sr.wasp.server.web;

public interface ProxyTask extends SinkTask, SourceTask {

    Proxy proxy();

}