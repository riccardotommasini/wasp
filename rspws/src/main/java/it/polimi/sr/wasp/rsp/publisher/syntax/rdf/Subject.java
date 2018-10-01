package it.polimi.sr.wasp.rsp.publisher.syntax.rdf;

import lombok.Getter;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Subject extends Resource {

    private List<Predicate> predicates = new ArrayList<>();

    public Subject(IRI node) {
        super(node);
    }

    public void predicate(IRI pred) {
        predicates.add(new Predicate(pred));
    }

    public void object(RDFTerm obj) {
        predicates.get(predicates.size() - 1).object(obj);
    }

}
