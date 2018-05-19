package it.polimi.rsp.test.mock;

import it.polimi.rsp.server.Server;
import it.polimi.rsp.vocals.VocalsUtils;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MockServer extends Server {

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            MockEngine csparql = new MockEngine("csparql", "http://example.org/");
            Model model = VocalsUtils.toVocals(csparql, csparql.getName(), csparql.getBase());
            model.write(new FileOutputStream(new File("./" + csparql.getName() + ".json")), "JSON-LD");
            new MockServer().start(csparql, args[0]);
        }
    }
}
