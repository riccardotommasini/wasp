package it.polimi.rsp.other;

import com.google.common.base.Strings;
import eu.optique.r2rml.api.binding.jena.JenaR2RMLMappingManager;
import eu.optique.r2rml.api.model.TriplesMap;
import it.unibz.inf.ontop.exception.MappingException;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPMapping;
import it.unibz.inf.ontop.spec.mapping.serializer.SQLPPMappingToR2RMLConverter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

public class OBDA2RML {

    private static String inputMappingFile;

    private static String owlFile;

    private static String outputMappingFile;

    public static void main(String[] args) throws IOException {

        inputMappingFile = "/Users/riccardo/_Projects/Teaching/MockupVocalService/src/main/resources/exampleBooks.obda";
        owlFile = "/Users/riccardo/_Projects/Teaching/MockupVocalService/src/main/resources/exampleBooks.owl";

        if (Strings.isNullOrEmpty(outputMappingFile)) {
            outputMappingFile = inputMappingFile.substring(0, inputMappingFile.length() - ".obda".length())
                    .concat(".ttl");
        }

        File out = new File(outputMappingFile);

        OntopSQLOWLAPIConfiguration.Builder configBuilder = OntopSQLOWLAPIConfiguration.defaultBuilder()
                .nativeOntopMappingFile(inputMappingFile)
                .jdbcDriver("dummy")
                .jdbcUrl("dummy")
                .jdbcUser("")
                .jdbcPassword("");

        if (!Strings.isNullOrEmpty(owlFile)) {
            configBuilder.ontologyFile(owlFile);
        }

        OntopSQLOWLAPIConfiguration config = configBuilder.build();

        SQLPPMapping ppMapping;
        /*
         * load the mapping in native Ontop syntax
         */
        try {
            ppMapping = config.loadProvidedPPMapping();
        } catch (MappingException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        OWLOntology ontology;
        try {
            ontology = config.loadInputOntology().orElse(null);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        SQLPPMappingToR2RMLConverter converter = new SQLPPMappingToR2RMLConverter(ppMapping, ontology);

        final Collection<TriplesMap> tripleMaps = converter.getTripleMaps();
//        final RDF4JR2RMLMappingManager mm = RDF4JR2RMLMappingManager.getInstance();
//        final RDF4JGraph rdf4JGraph = mm.exportMappings(tripleMaps);
//        final JenaRDF jena = new JenaRDF();
//        final Graph jenaGraph = jena.asJenaGraph(rdf4JGraph);

        final JenaR2RMLMappingManager mm = JenaR2RMLMappingManager.getInstance();
        final JenaGraph jenaGraph = mm.exportMappings(tripleMaps);
        final Graph graph = new JenaRDF().asJenaGraph(jenaGraph);

        try {
            // use Jena to output pretty turtle syntax
            RDFDataMgr.write(new FileOutputStream(out), graph, RDFFormat.TURTLE_PRETTY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("R2RML mapping file " + outputMappingFile + " written!");
    }
}
