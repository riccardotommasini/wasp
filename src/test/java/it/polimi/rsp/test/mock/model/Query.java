package it.polimi.rsp.test.mock.model;

import com.google.gson.Gson;
import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.server.web.Sink;
import it.polimi.rsp.server.web.Task;
import it.polimi.rsp.vocals.annotations.model.Deletable;
import it.polimi.rsp.vocals.annotations.model.Exposed;
import it.polimi.rsp.vocals.annotations.model.Key;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Observable;

@NoArgsConstructor
@Exposed(name = "queries")
@Deletable(name = "queries")
@AllArgsConstructor
public class Query extends Observable implements Task {

    @Key()
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
    public String toString() {
        return "{\"id\":" + "\"" + id + "\",\"body\":\"" + body + "\"}";
    }
}
