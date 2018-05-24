package it.polimi.test;

import it.polimi.sr.wasp.rsp.RSPEngine;
import it.polimi.sr.wasp.vocals.VocalsFactoryJena;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class Main {
    public static void main(String[] args) {

        Model model = VocalsFactoryJena.toVocals(RSPEngine.class);

        RDFDataMgr.write(System.out, model, RDFFormat.JSONLD_COMPACT_PRETTY);

        Model model2 = VocalsFactoryJena.toVocals(RSPEngine.class);

        RDFDataMgr.write(System.out, model2, RDFFormat.JSONLD_PRETTY);
    }
}
