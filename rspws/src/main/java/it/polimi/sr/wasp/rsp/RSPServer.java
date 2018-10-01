package it.polimi.sr.wasp.rsp;

import it.polimi.rsp.vocals.core.annotations.VocalsFactory;
import it.polimi.sr.wasp.rsp.server.ESDRequestHandler;
import it.polimi.sr.wasp.rsp.server.ObserverRequestHandler;
import it.polimi.sr.wasp.rsp.server.ObserversHandler;
import it.polimi.sr.wasp.server.Server;
import it.polimi.sr.wasp.utils.Config;
import lombok.extern.java.Log;

import static spark.Spark.path;
import static spark.Spark.port;

@Log
public abstract class RSPServer extends Server {

    public RSPServer(VocalsFactory factory) {
        super(factory);
    }

    public <T extends RSPActor> void start(T e, Config config) throws ClassNotFoundException {
        int port = config.getServerPort();
        String host = config.getHostName();
        String name = config.getServerName();
        Class<?> clazz = e.getClass().getSuperclass();
        log.info("Starting server " + name + ", class " + clazz.getSimpleName() + " at " + host + "," + port + "");
        stub = factory.toVocals(clazz, name);
        init(e, host, port, name, factory.fromVocals(stub));
    }

    @Override
    protected void ignite(String host, String name, int port) {
        port(port);
        path(name, () -> new ESDRequestHandler(stub, name).call());
        path(name, () -> new ObserversHandler().call());
        path(name, () -> new ObserverRequestHandler(name, host, port).call());
    }
}
