package it.polimi.sr.wasp.rsp;

import it.polimi.deib.rsp.vocals.jena.VocalsFactoryJena;
import it.polimi.sr.wasp.server.Server;
import it.polimi.sr.wasp.server.handlers.std.ObserverRequestHandler;
import it.polimi.sr.wasp.server.handlers.std.SGraphRequestHandler;
import it.polimi.sr.wasp.utils.Config;
import spark.utils.StringUtils;

import static spark.Spark.path;
import static spark.Spark.port;

public abstract class RSPServer extends Server {

    VocalsFactoryJena factoryJena = new VocalsFactoryJena();

    public void start(Object e, String engine_prop) {
        Config.initialize(engine_prop);
        int port = Config.getInstance().getServerPort();
        String host = Config.getInstance().getHostName();
        String name = Config.getInstance().getServerName();
        String base = "http://" + StringUtils.removeLeadingAndTrailingSlashesFrom(host) + ":" + port + "/" + name + "/";
        stub = VocalsFactoryJena.get().toVocals(RSPEngine.class);
        init(e, host, port, name, VocalsFactoryJena.get().fromVocals(stub));
    }

    @Override
    protected void ingnite(String host, String name, int port) {
        port(port);
        path(name, () -> new SGraphRequestHandler(stub).call());
        path(name, () -> new ObserverRequestHandler(name, host, port + 1).call());
    }
}
