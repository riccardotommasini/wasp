package it.polimi.rsp.triplewave;

import com.google.gson.Gson;
import lombok.extern.java.Log;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.StringWriter;

import static spark.Spark.*;

@Log
public class TripleWave {

    static final Gson gson = new Gson();
    static final Reflections reflections = new Reflections("it.polimi.rsp.mock");
    static PublishingService triplewave;

    public static void main(String[] args) throws IOException {

        triplewave = new PublishingService(4000, "triplewave", "http://localhost");
        Model model = ModelFactory.createDefaultModel().read("triplewave.json", "JSON-LD");
        port(triplewave.getPort());
        staticFileLocation("foo");
        get("", (request, response) -> getSGraph(model));
        get("tw1", (request, response) -> getSGraph(model));
        get("tw2", (request, response) -> getSGraph(model));
        get("tw3", (request, response) -> getSGraph(model));
        webSocket("/stream", triplewave);
        init();
    }

    private static String getSGraph(Model model) {
        StringWriter writer = new StringWriter();
        model.write(writer, "JSON-LD");
        return writer.toString();
    }




}

