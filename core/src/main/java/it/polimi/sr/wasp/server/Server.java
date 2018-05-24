package it.polimi.sr.wasp.server;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.VocalsStub;
import it.polimi.sr.wasp.server.handlers.RequestHandlerFactory;
import lombok.extern.java.Log;
import org.apache.jena.rdf.model.Model;

import java.util.List;

import static it.polimi.sr.wasp.server.MyService.path;


@Log
public abstract class Server {

    protected VocalsStub stub;

    protected void init(Object engine, String host, int port, String name, List<Endpoint> endpoints) {
        ingnite(host, name, port);
        endpoints.forEach(endpoint -> RequestHandlerFactory.getServices(engine, endpoint)
                .ifPresent(handler -> path(name, handler::call)));
        // endpoints.stream().filter(endpoint -> HttpMethod.GET.equals(endpoint.method)).forEach(endpoint -> RequestHandlerFactory.getGetters(engine, endpoint)
        //        .ifPresent(handler -> path(name, handler::call)));
        // endpoints.stream().filter(endpoint -> HttpMethod.DELETE.equals(endpoint.method)).forEach(endpoint -> RequestHandlerFactory.getDeleters(engine, endpoint)
        //      .ifPresent(handler -> path(name, handler::call)));
    }

    protected abstract void ingnite(String host, String path, int port);


}

