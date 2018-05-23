package it.polimi.rsp.test.mock;

import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.vocals.annotations.model.Deletable;
import it.polimi.rsp.vocals.annotations.model.Exposed;
import it.polimi.rsp.vocals.annotations.model.Key;
import lombok.extern.java.Log;

import java.util.Observable;
import java.util.Observer;

@Log
@Exposed(name = "streams")
@Deletable(name = "streams")
public class InStream extends Observable implements Stream {

    @Key()
    public String id;
    public String uri;

    public InStream(String id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "{\"stream\": \"" + id + "\", \"uri\": \"" + uri + "\"}";
    }


    @Override
    public String iri() {
        return uri;
    }

    @Override
    public void message(String msg) {
        log.info(id + " got a message " + msg);
        setChanged();
        notifyObservers(msg);
    }

    @Override
    public void observer(Observer observer) {
        addObserver(observer);
    }

}
