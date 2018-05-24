package it.polimi.sr.wasp.rsp;

import it.polimi.sr.wasp.server.Server;
import it.polimi.sr.wasp.server.handlers.std.ObserverRequestHandler;
import it.polimi.sr.wasp.server.handlers.std.SGraphRequestHandler;
import it.polimi.sr.wasp.utils.Config;
import it.polimi.sr.wasp.vocals.VocalsFactoryJena;
import spark.utils.StringUtils;

import static spark.Spark.path;
import static spark.Spark.port;

public abstract class RSPServer extends Server {

    public void start(Object e, String engine_prop) {
        Config.initialize(engine_prop);
        int port = Config.getInstance().getServerPort();
        String host = Config.getInstance().getHostName();
        String name = Config.getInstance().getServerName();
        String base = "http://" + StringUtils.removeLeadingAndTrailingSlashesFrom(host) + ":" + port + "/" + name + "/";
        init(e, host, port, name, VocalsFactoryJena.fromVocals(graph = VocalsFactoryJena.toVocals(RSPEngine.class)));
    }

    @Override
    protected void ingnite(String host, String name, int port) {
        port(port);
        path(name, () -> new SGraphRequestHandler(graph).call());
        path(name, () -> new ObserverRequestHandler(name, host, port + 1).call());
    }
}
