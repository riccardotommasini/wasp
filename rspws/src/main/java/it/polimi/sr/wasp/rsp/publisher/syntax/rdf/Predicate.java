package it.polimi.sr.wasp.rsp.publisher.syntax.rdf;

import lombok.Getter;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Predicate extends Resource {

    private List<RDFTerm> objects = new ArrayList<>();

    public Predicate(IRI node) {
        super(node);
    }

    public void object(RDFTerm obj) {
        objects.add(obj);
    }

}
