package it.polimi.sr.wasp.rsp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
public abstract class SPARQLUtils {

    private static final Pattern WINDOW = Pattern.compile("\\[\\s*RANGE\\s*(.*)\\s(STEP|SLIDE)\\s*(.*)\\s*\\]", Pattern.CASE_INSENSITIVE);
    private static final Pattern STREAM_WINDOW = Pattern.compile("FROM\\s*(?:NAMED)?\\sWINDOW\\s(.*)\\sON\\s\\<(.*?)\\>\\s", Pattern.CASE_INSENSITIVE);
    private static final Pattern STREAM_PATTERN1 = Pattern.compile("STREAM\\s*\\<(.*?)\\>\\s*", Pattern.CASE_INSENSITIVE);
    private static final Pattern GRAPH_PATTERN = Pattern.compile("FROM\\s*(?:NAMED)?\\s*(?:GRAPH)?\\s*\\<(.*?)\\>\\s", Pattern.CASE_INSENSITIVE);

    public static List<Window> extractWindows(String query) {

        Matcher m = WINDOW.matcher(query);

        List<Window> streams = new ArrayList<>();

        while (m.find()) {
            String alfa = m.group(1);
            String beta = m.group(3);
            streams.add(new Window(alfa, beta));
        }



        return streams;
    }


    public static List<String> extractStreams(String query) {

        Matcher m = STREAM_PATTERN1.matcher(query);

        List<String> streams = new ArrayList<>();

        while (m.find()) {
            String stream = m.group(1);
            streams.add(stream);
        }

        if (streams.size() == 0) {
            m = STREAM_WINDOW.matcher(query);
            while (m.find()) {
                String stream = m.group(2);
                streams.add(stream);
            }
        }

        return streams;
    }

    public static QueryType extractType(String query) {
        if (query.contains(QueryType.ASK.name())) {
            return QueryType.ASK;
        } else if (query.contains(QueryType.DESCRIBE.name())) {
            return QueryType.DESCRIBE;
        } else if (query.contains(QueryType.CONSTRUCT.name())) {
            return QueryType.CONSTRUCT;
        } else return QueryType.SELECT;
    }

    public static List<GraphClauses> extractGraphs(String query) {
        Matcher m = GRAPH_PATTERN.matcher(query);

        List<GraphClauses> graphs = new ArrayList<>();

        while (m.find()) {
            log.info(m.groupCount() + "");
            log.info(m.group(0));
            log.info(m.group(1));
            String stream = m.group(1);
            graphs.add(new GraphClauses(stream, m.group().contains("NAMED")));
        }

        return graphs;
    }

    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    public static class GraphClauses {

        public String iri;
        public boolean named;
    }

    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    public static class Window {

        public final String alpha;
        public final String beta;


    }
}
