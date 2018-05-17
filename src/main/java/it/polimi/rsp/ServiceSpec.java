package it.polimi.rsp;

import lombok.Getter;
import org.apache.jena.rdf.model.Model;

import java.util.List;


@Getter
public class ServiceSpec {

    private final Model model;
    private final int port;
    private final String name, base;
    private final List<Endpoint> endpoints;

    public ServiceSpec(Model model, String name, String base, int port) {
        this.model = model;
        this.port = port;
        this.base = base;
        this.name = name;
        this.endpoints = SpecUtils.getEndpoints(this.model);
    }
}
