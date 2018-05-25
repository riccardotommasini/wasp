package it.polimi.test;

import it.polimi.deib.rsp.vocals.rdf4j.VocalsFactoryRDF4J;
import org.apache.http.entity.ContentType;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.rio.jsonld.JSONLDWriterFactory;
import spark.Spark;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static spark.Spark.get;


public class Main {

    public static void main(String[] args) throws IOException {

        InputStream inputStream = new FileInputStream("/Users/riccardo/_Projects/RSP/wasp/rspws/src/test/resources/sgraph.ttl");

        RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);

        Model model = new LinkedHashModel();
        rdfParser.setRDFHandler(new StatementCollector(model));

        rdfParser.parse(inputStream, "http://www.example.org/vocals/examples#");


        Spark.port(4040);
        get("sgraph", (request, response) -> {

            StringWriter out = new StringWriter();
            JSONLDWriterFactory jsonldWriterFactory = new JSONLDWriterFactory();
            RDFWriter rdfWriter = jsonldWriterFactory.getWriter(out);
            VocalsFactoryRDF4J.prefixMap.forEach(rdfWriter::handleNamespace);
            rdfWriter.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
            rdfWriter.startRDF();
            model.forEach(rdfWriter::handleStatement);
            rdfWriter.endRDF();

            response.type(ContentType.APPLICATION_JSON.getMimeType());
            return out.toString();
        });
    }
}
