package it.polimi.rsp.server;

import it.polimi.rsp.server.handlers.RequestHandlerFactory;
import it.polimi.rsp.server.handlers.std.ObserverRequestHandler;
import it.polimi.rsp.server.handlers.std.SGraphRequestHandler;
import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.server.model.StatusManager;
import it.polimi.rsp.utils.Config;
import it.polimi.rsp.vocals.VocalsUtils;
import lombok.extern.java.Log;
import org.apache.jena.rdf.model.Model;
import spark.utils.StringUtils;

import java.util.List;

import static it.polimi.rsp.server.MyService.path;
import static it.polimi.rsp.server.MyService.port;


@Log
public abstract class Server {

    public void start(Object e, String engine_prop) {
        Config.initialize(engine_prop);
        int port = Config.getInstance().getServerPort();
        String host = Config.getInstance().getHostName();
        String name = Config.getInstance().getServerName();
        String base = "http://" + StringUtils.removeLeadingAndTrailingSlashesFrom(host) + ":" + port + "/" + name + "/";
        Model model = VocalsUtils.toVocals(e, name, base);
        StatusManager.get();
        init(e, host, port, name, model, VocalsUtils.fromVocals(model));
    }

    private void init(Object engine, String host, int port, String name, Model m, List<Endpoint> endpoints) {
        port(port);
        path(name, () -> new SGraphRequestHandler(m).call());
        path(name, () -> new ObserverRequestHandler(name, host, port + 1).call());
        endpoints.forEach(endpoint -> RequestHandlerFactory.getServices(engine, endpoint)
                .ifPresent(handler -> path(name, handler::call)));
        // endpoints.stream().filter(endpoint -> HttpMethod.GET.equals(endpoint.method)).forEach(endpoint -> RequestHandlerFactory.getGetters(engine, endpoint)
        //        .ifPresent(handler -> path(name, handler::call)));
        // endpoints.stream().filter(endpoint -> HttpMethod.DELETE.equals(endpoint.method)).forEach(endpoint -> RequestHandlerFactory.getDeleters(engine, endpoint)
        //      .ifPresent(handler -> path(name, handler::call)));
    }


}


