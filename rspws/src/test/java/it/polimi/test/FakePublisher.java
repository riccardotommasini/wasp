package it.polimi.test;

import it.polimi.deib.rsp.vocals.rdf4j.VocalsFactoryRDF4J;
import it.polimi.sr.wasp.rsp.RSPServer;
import it.polimi.sr.wasp.rsp.publisher.RSPPublisher;
import it.polimi.sr.wasp.utils.Config;
import lombok.extern.java.Log;

import java.io.IOException;

@Log
public class FakePublisher extends RSPPublisher {

    public FakePublisher(String name, String base) {
        super(name, base);
    }

    @Log
    private static class Main extends RSPServer {

        public Main() throws IOException {
            super(new VocalsFactoryRDF4J());
        }

        public static void main(String[] args) throws IOException, ClassNotFoundException {
            if (args.length > 0) {

                Config.initialize(args[0]);
                Config config = Config.getInstance();
                int port = config.getServerPort();
                String host = config.getHostName();
                String name = config.getServerName();

                FakePublisher publisher = new FakePublisher(name, "http://" + host + ":" + port);
                new Main().start(publisher, config);
            }
        }
    }

}
