import it.polimi.rsp.mock.Engine;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.IOException;
import java.nio.charset.Charset;

@Log
public class Server2 {
    public static void main(String[] args) throws IOException {

        Model model = ModelFactory.createDefaultModel().read(
                Server2.class.getClassLoader().getResourceAsStream("csparql.json"), "http://example.org/", "JSON-LD");

        String qstring = IOUtils.toString(Server2.class.getClassLoader().getResourceAsStream("engine.sparql"), Charset.defaultCharset());

        Query q = QueryFactory.create(qstring);

        QueryExecution queryExecution = QueryExecutionFactory.create(q, model);

        ResultSet res = queryExecution.execSelect();

        if (res.hasNext()) {
            QuerySolution s = res.next();
            new Engine(s.get("?name"),s.get("?base"),s.get("?id"));
        }

        model.write(System.out, "TTL");

    }

}
