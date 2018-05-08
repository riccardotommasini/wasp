import it.polimi.rsp.mock.Engine;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

@Log
public class Server {
    public static void main(String[] args) throws IOException {

        Model model = ModelFactory.createDefaultModel().read(
                Server2.class.getClassLoader().getResourceAsStream("csparql.json"), "http://example.org/", "JSON-LD");

        Engine e = getEngine(model);
        List<Endpoint> endpoints = getEndpoints(model);

        port(8182);
        path(e.name, () -> {
            get("", (req, res) -> Server.class.getClassLoader().getResourceAsStream(e.name + ".json"));

            endpoints.forEach(endpoint -> {
                        if ("GET".equals(endpoint.method)) {
                            log.info(endpoint.uri);
                            get(endpoint.uri, (request, response) -> Server.class.getClassLoader().getResourceAsStream(endpoint.name.toLowerCase() + ".json"));
                        } else if ("POST".equals(endpoint.method))
                            post(endpoint.uri, (request, response) -> {
                                response.status(200);
                                return response;
                            });
                    }
            );
        });
        log.info("Server Running on port 8182");
    }


    private static Engine getEngine(Model model) throws IOException {

        String qstring = IOUtils.toString(Server2.class.getClassLoader().getResourceAsStream("engine.sparql"), Charset.defaultCharset());

        Query q = QueryFactory.create(qstring);

        QueryExecution queryExecution = QueryExecutionFactory.create(q, model);

        ResultSet res = queryExecution.execSelect();

        if (res.hasNext()) {
            QuerySolution s = res.next();
            return new Engine(s.get("?name"), s.get("?base"), s.get("?id"));
        }
        throw new RuntimeException();
    }

    private static List<Endpoint> getEndpoints(Model model) throws IOException {

        List<Endpoint> list = new ArrayList<>();

        String qstring = IOUtils.toString(Server2.class.getClassLoader().getResourceAsStream("endpoints.sparql"), Charset.defaultCharset());
        String q1string = IOUtils.toString(Server2.class.getClassLoader().getResourceAsStream("params.sparql"), Charset.defaultCharset());

        Query q = QueryFactory.create(qstring);
        Query q1 = QueryFactory.create(q1string);

        QueryExecution queryExecution = QueryExecutionFactory.create(q, model);

        ResultSet res = queryExecution.execSelect();

        while (res.hasNext()) {
            QuerySolution s = res.next();
            list.add(new Endpoint(s.get("?name"), s.get("?endpoint"), s.get("?method")));
        }

        list.forEach(System.out::println);
        return list;

    }
}

