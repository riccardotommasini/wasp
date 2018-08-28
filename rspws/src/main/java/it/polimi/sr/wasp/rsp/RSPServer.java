package it.polimi.sr.wasp.rsp;

import it.polimi.rsp.vocals.core.annotations.VocalsFactory;
import it.polimi.sr.wasp.server.Server;
import it.polimi.sr.wasp.rsp.server.ESDRequestHandler;
import it.polimi.sr.wasp.rsp.server.ObserverRequestHandler;
import it.polimi.sr.wasp.rsp.server.ObserversHandler;
import it.polimi.sr.wasp.utils.Config;

import static spark.Spark.path;
import static spark.Spark.port;

public abstract class RSPServer extends Server {

    public RSPServer(VocalsFactory factory) {
        super(factory);
    }

    public void start(RSPEngine e, String engine_prop) {
        Config.initialize(engine_prop);
        int port = Config.getInstance().getServerPort();
        String host = Config.getInstance().getHostName();
        String name = Config.getInstance().getServerName();
        stub = factory.toVocals(RSPEngine.class, e.getName());
        init(e, host, port, name, factory.fromVocals(stub));
    }

    @Override
    protected void ignite(String host, String name, int port) {
        port(port);
        path(name, () -> new ESDRequestHandler(stub).call());
        path(name, () -> new ObserversHandler().call());
        path(name, () -> new ObserverRequestHandler(name, host, port).call());
    }
}
