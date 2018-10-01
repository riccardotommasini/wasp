package it.polimi.sr.wasp.rsp.publisher.syntax.rdf;

import lombok.AllArgsConstructor;
import org.apache.commons.rdf.api.IRI;

@AllArgsConstructor
public class Resource {

    public final IRI node;

    @Override
    public String toString() {
        return node.toString();
    }
}
