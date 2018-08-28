package it.polimi.sr.wasp.rsp.model;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.description.DescriptorHashMap;
import it.polimi.sr.wasp.server.model.concept.Task;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor
@RequiredArgsConstructor
public class ObservableTask extends Observable implements Task {

    public final String id;
    public final String body;
    public final String base;

    protected Channel out;
    protected List<Channel> in = new ArrayList<>();

    @Override
    public Channel out() {
        return out;
    }

    @Override
    public Channel[] in() {
        return in.toArray(new Channel[in.size()]);
    }

    @Override
    public String iri() {
        return id;
    }

    public void add(Channel in) {
        this.in.add(in);
    }

    @Override
    public String toString() {
        try {
            return getJson();
        } catch (IOException e) {
            e.printStackTrace();
            return "{\n" +
                    "  \"@context\": {\n" +
                    "    \"@base\":\"" + base + "\",\n" +
                    "    \"vocals\": \"http://w3id.org/rsp/vocals#\",\n" +
                    "    \"vprov\": \"http://w3id.org/rsp/vocals-prov#\",\n" +
                    "    \"vsd\": \"http://w3id.org/rsp/vocals-sd#\",\n" +
                    "    \"xsd\": \"http://www.w3.org/2001/XMLSchema#\"\n" +
                    "    \"rdfs\": \"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                    "    \"rdf\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                    "  },\n" +
                    "  \"@id\": \"" + iri() + "\",\n" +
                    "  \"@type\":\"\",\n" +
                    "  \"vprov:body\": \"" + body + "\"\n" +
                    "  \"rdfs:seeAlso\": \"" + out.iri() + "\"\n" +
                    "}";
        }

    }

    private String getJson() throws IOException {
        JsonLdOptions options = new JsonLdOptions();
        Map<String, Object> context = new HashMap<>();
        context.put("@base", base + URIUtils.SLASH);
        context.put("vocals", "http://w3id.org/rsp/vocals#");
        context.put("vprov", "http://w3id.org/rsp/vocals-prov#");
        context.put("vsd", "http://w3id.org/rsp/vocals-sd#");
        context.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        context.put("format", "http://www.w3.org/ns/formats/");
        context.put("dcat", "https://www.w3.org/TR/vocab-dcat/");
        context.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        context.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        context.put("prov", "http://www.w3.org/ns/prov#");

        Map<String, Object> task = new HashMap<>();

        task.put("@id", iri());
        task.put("@type", "vprov:Task");
        task.put("prov:generated", new DescriptorHashMap() {{
            put("@id", out.iri());
        }});

        in.forEach(i -> task.put("prov:uses", new DescriptorHashMap() {{
            put("@id", i.iri());
        }}));

        String s = JsonUtils.toPrettyString(JsonLdProcessor.compact(task, context, options));
        return s;
    }

}
