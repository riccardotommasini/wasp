package it.polimi.sr.wasp.test.vocals;

import it.polimi.sr.wasp.test.mock.RSPEngine;
import it.polimi.sr.wasp.vocals.VocalsFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SerializationTest {

    @Test
    public void test1() {
        Model gen = VocalsFactory.toVocals(RSPEngine.class, "csparql", "http://example.org/");
        RDFDataMgr.write(System.out, gen,RDFFormat.JSONLD_PRETTY);


    }

}
