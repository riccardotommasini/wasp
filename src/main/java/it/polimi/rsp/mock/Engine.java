package it.polimi.rsp.mock;

import org.apache.jena.rdf.model.RDFNode;

public class Engine {

    public final String name, base, id;

    public Engine(RDFNode name, RDFNode base, RDFNode id) {
        this.name = name.toString();
        this.base= base.toString();
        this.id=id.toString();
    }
}
