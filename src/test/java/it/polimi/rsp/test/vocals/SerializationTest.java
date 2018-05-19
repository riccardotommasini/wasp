package it.polimi.rsp.test.vocals;

import it.polimi.rsp.test.mock.MockEngine;
import it.polimi.rsp.vocals.VocalsUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SerializationTest {

    @Test
    public void test1() {
        Model gen = VocalsUtils.toVocals(MockEngine.class, "csparql", "http://example.org/");
        RDFDataMgr.write(System.out, gen,RDFFormat.JSONLD_PRETTY);


    }

}
