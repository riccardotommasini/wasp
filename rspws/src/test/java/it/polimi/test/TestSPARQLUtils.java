package it.polimi.test;

import it.polimi.sr.wasp.utils.SPARQLUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestSPARQLUtils {
    @Test
    public void TestExtractGraphCSPARQL() {

        String queryBody = "REGISTER QUERY staticKnowledge AS "
                + "PREFIX :<http://www.streamreasoning.org/ontologies/sr4ld2014-onto#> "
                + "SELECT ?p1 ?r1 "
                + "FROM STREAM <http://streamreasoning.org/channels/fb> [RANGE 1s STEP 1s] "
                + "FROM <http://streamreasoning.org/roomConnection> "
                + "FROM NAMED <http://streamreasoning.org/roomConnection2> "
                + "WHERE { "
                + "?p :posts [ :who ?p1 ; :where ?r ] . "
                + "?r :isConnectedTo ?r1 . "
                + "} ";

        List<SPARQLUtils.GraphClauses> graphClauses = SPARQLUtils.extractGraphs(queryBody);

        SPARQLUtils.GraphClauses g1 = new SPARQLUtils.GraphClauses("http://streamreasoning.org/roomConnection", false);
        SPARQLUtils.GraphClauses g2 = new SPARQLUtils.GraphClauses("http://streamreasoning.org/roomConnection2", true);

        assertEquals(g1, graphClauses.get(0));
        assertEquals(g2, graphClauses.get(1));

        List<String> strings = SPARQLUtils.extractStreams(queryBody);

        assertEquals("http://streamreasoning.org/channels/fb", strings.get(0));
    }

    @Test
    public void TestExtractGraphCQESL() {

        String queryBody = "REGISTER QUERY staticKnowledge AS "
                + "PREFIX :<http://www.streamreasoning.org/ontologies/sr4ld2014-onto#> "
                + "SELECT ?p1 ?r1 "
                + "FROM <http://streamreasoning.org/roomConnection> "
                + "FROM NAMED <http://streamreasoning.org/roomConnection2> "
                + "WHERE { " +
                "   STREAM <http://streamreasoning.org/channels/fb> [RANGE 1s STEP 1s] {"
                + "?p :posts [ :who ?p1 ; :where ?r ] . "
                + "?r :isConnectedTo ?r1 . }"
                + "} ";

        List<SPARQLUtils.GraphClauses> graphClauses = SPARQLUtils.extractGraphs(queryBody);

        SPARQLUtils.GraphClauses g1 = new SPARQLUtils.GraphClauses("http://streamreasoning.org/roomConnection", false);
        SPARQLUtils.GraphClauses g2 = new SPARQLUtils.GraphClauses("http://streamreasoning.org/roomConnection2", true);

        assertEquals(g1, graphClauses.get(0));
        assertEquals(g2, graphClauses.get(1));

        List<String> strings = SPARQLUtils.extractStreams(queryBody);

        assertEquals("http://streamreasoning.org/channels/fb", strings.get(0));

    }

    @Test
    public void TestExtractGraphRSPQL() {

        String queryBody = "PREFIX e: <http://somevocabulary.org/> \n" +
                " PREFIX s: <http://someinvasivesensornetwork.org/channels#>\n" +
                " PREFIX g: <http://somesocialnetwork.org/graphs#>\n" +
                " PREFIX : <http://acrasycompany.org/rsp>\n" +
                " REGISTER STREAM :GallehaultWasTheBar \n" +
                " UNDER ENTAILMENT REGIME <http://www.w3.org/ns/entailment/RIF>\n" +
                " AS\n" +
                " CONSTRUCT ISTREAM { \n" +
                "  ?poi rdf:type :Gallehault ; \n" +
                "       :count ?howmanycouples ;\n" +
                "       :for (?somebody ?someoneelse)   \t\t\t\t\t\t \n" +
                " } \n" +
                " FROM NAMED WINDOW :veryLongWindow ON <http://someinvasivesensornetwork.org/channels#1> [RANGE PT4H STEP PT1H] \n" +
                " FROM NAMED WINDOW :longWindow ON <http://someinvasivesensornetwork.org/channels#1> [FROM NOW-PT35M TO NOW-PT5M STEP PT5M] \n" +
                " FROM NAMED WINDOW :shortWindow ON <http://someinvasivesensornetwork.org/channels#1> [RANGE PT10M STEP PT5M]\n" +
                " FROM NAMED GRAPH <http://somesocialnetwork.org/graphs#SocialGraph>\n" +
                " FROM GRAPH <http://somesocialnetwork.org/graphs#POIs>\n" +
                " WHERE {\n" +
                "  ?poi rdf:type e:bar . \n" +
                "  WINDOW :veryLongWindow {\n" +
                "        {?somebody e:enters ?poi} BEGIN AT ?t3\n" +
                "        {?someoneelse e:enters ?poi} BEGIN AT ?t4\n" +
                "        FILTER(?t3>?t4) \n" +
                "  }\n" +
                "  WINDOW :longWindow {\n" +
                "      {\n" +
                "        ?somebody e:isCloseTo ?someoneelse \n" +
                "        MINUS { ?somebody e:isCloseTo ?yetanotherone . FILTER (?yetanotherone != ?someoneelse) } \n" +
                "      } WITH DURATION ?duration\n" +
                "      FILTER (?duration>=\"PT30M\"^^xsd:duration)\n" +
                "  }\n" +
                "  WINDOW :shortWindow {\n" +
                "      { ?somebody e:exits ?bar} BEGIN AT ?t1\n" +
                "      { ?someoneelse e:exits ?bar } BEGIN AT ?t2 \n" +
                "      FILTER (abs(?t2-?t1)<\"PT1M\"^^xsd:duration )\n" +
                "  }\n" +
                "  GRAPH g:SocialGraph { \n" +
                "      FILTER NOT EXIST { ?somebody e:knows ?someoneelse }\n" +
                "  }\n" +
                "  FILTER (?somebody != ?someoneelse)\n" +
                " }\n" +
                " AGGREGATE {\n" +
                "  GROUP BY ?poi \n" +
                "  COUNT(?somebody) AS ?howmanycouples \n" +
                " }";

        List<SPARQLUtils.GraphClauses> graphClauses = SPARQLUtils.extractGraphs(queryBody);

        SPARQLUtils.GraphClauses g1 = new SPARQLUtils.GraphClauses("http://somesocialnetwork.org/graphs#POIs", false);
        SPARQLUtils.GraphClauses g2 = new SPARQLUtils.GraphClauses("http://somesocialnetwork.org/graphs#SocialGraph", true);

        assertEquals(g1, graphClauses.get(1));
        assertEquals(g2, graphClauses.get(0));

        List<String> strings = SPARQLUtils.extractStreams(queryBody);

        assertEquals("http://someinvasivesensornetwork.org/channels#1", strings.get(0));

    }
}
