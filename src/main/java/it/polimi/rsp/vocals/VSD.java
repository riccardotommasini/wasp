package it.polimi.rsp.vocals;

import lombok.Getter;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class VSD {
    @Getter
    private static String uri = "http://w3id.org/rsp/vocals-sd#";

    public final static Property hasService;
    public final static Property name;
    public final static Resource uri_param;
    public final static Resource body_param;
    public final static Property endpoint;
    public final static Property method;
    public final static Resource ProcessingService;
    public final static Property base;
    public final static Property params;
    public final static Property index;

    static {
        ProcessingService = resource("ProcessingService");
        base = property("base");
        hasService = property("hasService");
        name = property("name");
        uri_param = resource("uri_param");
        body_param = resource("body_param");
        endpoint = property("endpoint");
        method = property("method");
        params = property("params");
        index = property("index");
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }

}
