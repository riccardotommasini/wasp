package it.polimi.sr.wasp.rsp.model;

import com.google.gson.Gson;
import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.server.web.Sink;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

@RequiredArgsConstructor
public class QueryStream implements Stream{

    private final String id;
    private final String uri;

    private List<Sink> writers = new ArrayList<>();

    @Override
    public String toString() {
        return new Gson().toJson(this, QueryStream.class);
    }

    public void update(String message) {
        writers.forEach(w -> w.message(message));
        //TODO receives message from the message
    }

    public void add(Sink w) {
        writers.add(w);
    }

    @Override
    public String iri() {
        return null;
    }

    @Override
    public void message(String task) {

    }

    @Override
    public void observer(Observer observer) {

    }
}
