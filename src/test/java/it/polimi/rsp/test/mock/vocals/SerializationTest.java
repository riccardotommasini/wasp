package it.polimi.rsp.test.mock.vocals;

import it.polimi.rsp.vocals.annotations.VocalsFactory;
import it.polimi.rsp.test.mock.MockEngine;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

public class SerializationTest {

    @Test
    public void test1() {

        Model model = ModelFactory.createDefaultModel().read(
                VocalsFactory.class.getClassLoader().getResourceAsStream("csparql.json"), "http://example.org/", "JSON-LD");

        model.write(System.out, "TTL");
        VocalsFactory.toVocals(MockEngine.class, "csparql", "http://example.org/").write(System.out, "TTL");

    }

}
