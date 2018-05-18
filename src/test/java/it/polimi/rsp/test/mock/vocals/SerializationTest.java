package it.polimi.rsp.test.mock.vocals;

import it.polimi.rsp.test.mock.MockEngine;
import it.polimi.rsp.vocals.annotations.VocalsUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SerializationTest {

    @Test
    public void test1() {
        Model gen = VocalsUtils.toVocals(MockEngine.class, "csparql", "http://example.org/");
        gen.write(System.err, "JSON-LD");

        Model ref = ModelFactory.createDefaultModel().read(
                 SerializationTest.class.getClassLoader().getResourceAsStream("mock.json"), "http://example.org/", "JSON-LD");

        ref.write(System.out, "TTL");

    }

}
