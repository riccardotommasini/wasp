package it.polimi.rsp;

import it.polimi.rsp.vocals.VSD;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Log
public class SpecUtils {

    public static List<Endpoint> getEndpoints(Model model) {

        try {
            List<Endpoint> list = new ArrayList<>();

            String qstring = IOUtils.toString(SpecUtils.class.getClassLoader().getResourceAsStream("endpoints.sparql"), Charset.defaultCharset());
            String q1string = IOUtils.toString(SpecUtils.class.getClassLoader().getResourceAsStream("params.sparql"), Charset.defaultCharset());

            Query q = QueryFactory.create(qstring);
            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(q1string);

            QueryExecution queryExecution = QueryExecutionFactory.create(q, model);

            ResultSet res = queryExecution.execSelect();

            while (res.hasNext()) {

                List<Endpoint.Par> params = new ArrayList<>();
                QuerySolution s = res.next();
                Endpoint e = new Endpoint(s.get("?name"), s.get("?endpoint"), s.get("?method"));

                pss.setParam("?feature", s.get("?feature"));

                log.info(pss.toString());

                Query query = pss.asQuery();
                ResultSet param = QueryExecutionFactory.create(query, model).execSelect();

                while (param.hasNext()) {
                    QuerySolution next = param.next();
                    String name = next.get("?name").toString();
                    int index = Integer.parseInt(next.get("?index").toString());
                    boolean uri = next.get("?type").equals(VSD.uri_param);
                    params.add(new Endpoint.Par(name, index, uri));
                }

                params.sort((o1, o2) -> o1.index < o2.index ? -1 : (
                        o1.index == o2.index ? 0 : -1));

                e.params = params.toArray(new Endpoint.Par[params.size()]);

                if (s.contains("?feature")) {
                    e.feature = s.get("?feature").toString();
                } else if (s.contains("?method") && "GET".equals(s.get("?method").toString())) {
                    e.feature = e.params.length > 1 ? ":GetterFeatureN" : ":GetterFeature" + e.params.length;
                }
                list.add(e);
            }

            list.forEach(System.out::println);
            return list;
        } catch (IOException e) {
            return new ArrayList<>();
        }

    }
}
