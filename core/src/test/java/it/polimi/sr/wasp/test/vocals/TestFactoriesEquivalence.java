package it.polimi.sr.wasp.test.vocals;

import it.polimi.sr.wasp.test.vocals.model.MockDouble;
import it.polimi.sr.wasp.test.vocals.model.MockSingles;
import it.polimi.sr.wasp.vocals.VocalsFactoryJena;
import it.polimi.sr.wasp.vocals.VocalsFactoryRDF4J;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.eclipse.rdf4j.rio.jsonld.JSONLDWriterFactory;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class TestFactoriesEquivalence {
    @Test
    public void jena() {

        Model mock_double = VocalsFactoryJena.toVocals(MockDouble.class);
        Model mock_singles = VocalsFactoryJena.toVocals(MockSingles.class);

        RDFDataMgr.write(System.out, mock_double, RDFFormat.JSONLD_COMPACT_PRETTY);
        RDFDataMgr.write(System.out, mock_singles, RDFFormat.JSONLD_COMPACT_PRETTY);
        assertTrue(mock_double.isIsomorphicWith(mock_singles));

    }

    @Test
    public void rdf4j() {

        org.eclipse.rdf4j.model.Model mock_double = VocalsFactoryRDF4J.toVocals(MockDouble.class);
        org.eclipse.rdf4j.model.Model mock_singles = VocalsFactoryRDF4J.toVocals(MockSingles.class);

        JSONLDWriterFactory jsonldWriterFactory = new JSONLDWriterFactory();
        RDFWriter rdfWriter = jsonldWriterFactory.getWriter(System.out);
        VocalsFactoryRDF4J.prefixMap.forEach(rdfWriter::handleNamespace);
        rdfWriter.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
        rdfWriter.startRDF();
        mock_double.forEach(rdfWriter::handleStatement);
        rdfWriter.endRDF();

        RDFWriter rdfWriter2 = jsonldWriterFactory.getWriter(System.err);
        VocalsFactoryRDF4J.prefixMap.forEach(rdfWriter2::handleNamespace);
        rdfWriter2.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
        rdfWriter2.startRDF();
        mock_singles.forEach(rdfWriter2::handleStatement);
        rdfWriter2.endRDF();

        assertTrue(Models.isomorphic(mock_double, mock_singles));
    }

}
