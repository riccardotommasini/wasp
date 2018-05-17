package it.polimi.rsp;

import lombok.ToString;
import org.apache.jena.rdf.model.RDFNode;

@ToString
public class Endpoint {

    public final String name, uri, method;
    public String feature;
    public Par[] params;

    public Endpoint(RDFNode name, RDFNode url, RDFNode method) {
        this.name = name.toString();
        this.uri = url.toString();
        this.method = method.toString();
    }

    public static class Par {
        public final int index;
        public final String name;
        public final boolean uri;

        public Par(String name, int index, boolean uri) {
            this.index = index;
            this.name = name;
            this.uri = uri;
        }
    }
}
