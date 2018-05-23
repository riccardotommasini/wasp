package it.polimi.rsp.test.vocals;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDatasetUtils;
import com.google.gson.Gson;
import it.polimi.rsp.test.mock.MockEngine;
import it.polimi.rsp.vocals.VocalsFactory;
import lombok.ToString;
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class SerializerTest {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, JsonLdError {
        MockEngine engine = new MockEngine("csparql", "http://localhost");

        Graph x = VocalsFactory.toVocals2(engine.getClass());
        RDF rdf = new SimpleRDF();
        BlankNode blankNode = rdf.createBlankNode();

        String collect = x.stream()
                .map(o -> rdf.createQuad(blankNode, o.getSubject(), o.getPredicate(), o.getObject()))
                .map(Quad::toString).collect(Collectors.joining("\t"));

        System.out.println(collect);
        RDFDataset rdfDataset = RDFDatasetUtils.parseNQuads(collect);

        System.out.println(rdfDataset);
    }

    @Test
    public void test() {

        String p = "{\"name\":\"Ricardo\", \"age\":27}";
        String a = "{\"race\":\"Doberman\", \"age\":15}";

        Gson gson = new Gson();
        Person person = gson.fromJson(p, Person.class);
        Animal animal = gson.fromJson(a, Animal.class);

        System.out.println(person);
        System.out.println(animal);

        String d = "{{\"name\":\"Ricardo\", \"age\":27}, {\"race\":\"Doberman\", \"age\":15}}";

        person = gson.fromJson(d, Person.class);
        animal = gson.fromJson(d, Animal.class);

        System.out.println(person);
        System.out.println(animal);

        String c = "{\"name\":\"Ricardo\", \"race\":\"Doberman\", \"age\":15}";

        person = gson.fromJson(c, Person.class);
        animal = gson.fromJson(c, Animal.class);

        System.out.println(person);
        System.out.println(animal);

    }


    @ToString
    class Person {
        public String name;
        public Integer age;
    }

    @ToString
    class Animal {
        public String race;
        public Integer age;
    }

}
