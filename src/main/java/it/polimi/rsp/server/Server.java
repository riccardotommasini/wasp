package it.polimi.rsp.server;

import it.polimi.rsp.server.handlers.RequestHandlerFactory;
import it.polimi.rsp.server.handlers.rsp.SGraphRequestHandler;
import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.utils.Config;
import it.polimi.rsp.vocals.VocalsUtils;
import lombok.extern.java.Log;
import org.apache.jena.rdf.model.Model;
import spark.utils.StringUtils;

import java.util.List;

import static spark.Spark.path;
import static spark.Spark.port;

@Log
public abstract class Server {

    public void start(Object e, String engine_prop) {
        Config.initialize(engine_prop);
        int port = Config.getInstance().getServerPort();
        String host = Config.getInstance().getHostName();
        String name = Config.getInstance().getServerName();
        String base = "http://" + StringUtils.removeLeadingAndTrailingSlashesFrom(host) + ":" + port + "/" + name + "/";
        Model model = VocalsUtils.toVocals(e, name, base);
        init(e, port, name, model, VocalsUtils.fromVocals(model));
    }

    private void init(Object engine, int port, String name, Model m, List<Endpoint> endpoints) {
        port(port);
        path(name, () -> new SGraphRequestHandler(m).call());
        endpoints.forEach(endpoint -> RequestHandlerFactory.getHandler(engine, endpoint)
                .ifPresent(handler -> path(name, handler::call)));
    }


}


