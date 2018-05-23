package it.polimi.rsp.server.handlers.std;

import it.polimi.rsp.server.HttpMethod;
import it.polimi.rsp.server.handlers.AbstractReflectiveRequestHandler;
import it.polimi.rsp.server.model.Endpoint;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import spark.Request;
import spark.Response;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import static spark.Spark.get;

@Log
public class SGraphRequestHandler extends AbstractReflectiveRequestHandler {

    private Model model;

    public SGraphRequestHandler(Model m) {
        super(null, new Endpoint("sgraph", "", HttpMethod.GET, "sgraph", new Endpoint.Par[]{}));
        this.model = m;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type(ContentType.APPLICATION_JSON.getMimeType());
        return getDescription(model);
    }

    @Override
    public void call() {
        log.info("SGRAPH Endpoint GET: [" + endpoint.uri + "] Ready");
        get(endpoint.uri, ContentType.APPLICATION_JSON.getMimeType(), this);
    }

    private String getDescription(Model model) throws UnsupportedEncodingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        RDFDataMgr.write(os, model, RDFFormat.JSONLD_PRETTY);
        return new String(os.toByteArray(), "UTF-8");
    }
}
