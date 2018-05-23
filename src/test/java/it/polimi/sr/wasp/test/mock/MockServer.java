package it.polimi.sr.wasp.test.mock;

import it.polimi.sr.wasp.server.Server;
import it.polimi.sr.wasp.vocals.VocalsFactory;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MockServer extends Server {

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            RSPEngine csparql = new RSPEngine("csparql", "http://localhost:8181/csparql");
            Model model = VocalsFactory.toVocals(csparql, csparql.getName(), csparql.getBase());
            model.write(new FileOutputStream(new File("./" + csparql.getName() + ".json")), "JSON-LD");
            new MockServer().start(csparql, args[0]);
        }
    }
}
