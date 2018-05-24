package it.polimi.test;

import it.polimi.sr.wasp.rsp.RSPEngine;
import it.polimi.sr.wasp.rsp.RSPServer;
import it.polimi.sr.wasp.rsp.model.InStream;
import it.polimi.sr.wasp.rsp.model.Query;
import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.vocals.VocalsFactoryJena;
import lombok.extern.java.Log;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Log
public class FakeEngine extends RSPEngine {

    public FakeEngine(String name, String base) {
        super(name, base);
    }

    @Override
    protected Query handleInternalQuery(String queryUri, String body) {
        return new FakeQuery(queryUri, body);
    }

    @Override
    protected Stream handleInternalStream(String id, String body) {
        return new InStream(id, body);
    }

    private class FakeQuery extends Query {
        private final String uri;

        public FakeQuery(String uri, String body) {
            this.uri = uri;
            this.body = body;
        }
    }

    @Log
    private static class Main extends RSPServer {
        public static void main(String[] args) throws IOException {
            if (args.length > 0) {
                RSPEngine csparql = new FakeEngine("fake", "http://localhost:8181/fake");
                Model model = VocalsFactoryJena.toVocals(RSPEngine.class);
                model.write(new FileOutputStream(new File("./" + csparql.getName() + ".json")), "JSON-LD");
                new Main().start(csparql, args[0]);
            }
        }
    }
}
