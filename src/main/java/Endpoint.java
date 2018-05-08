import lombok.ToString;
import org.apache.jena.rdf.model.RDFNode;

@ToString
public class Endpoint {

    public final String name, uri, method;
    public String body;

    public Endpoint(RDFNode name, RDFNode url, RDFNode method) {
        this.name = name.toString();
        this.uri = url.toString();
        this.method = method.toString();
    }
}
