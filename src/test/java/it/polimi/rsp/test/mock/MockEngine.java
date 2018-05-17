package it.polimi.rsp.test.mock;

import it.polimi.rsp.SREngine;
import it.polimi.rsp.vocals.annotations.Base;
import it.polimi.yasper.core.stream.rdf.RDFStream;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log
@Getter
@Base(base = "http://localhost:8182/")
public class MockEngine extends SREngine implements MockFeature {

    private final String base;
    private final String name;

    public MockEngine(String name, String base) {
        this.name = name;
        this.base = base;
    }

    @Override
    public MockReturnClass customMethod(String id, MockInputClass cc) {
        log.info(cc.id + " " + cc.body);
        return new MockReturnClass(cc.body, cc.id);
    }

    public void customMethod(Map<String, String> cc) {
        cc.entrySet().forEach(e -> log.info(e.getKey() + " " + e.getValue()));
    }

    public void customMethod2(MockReturnClass cc) {
        log.info("Custom3" + cc.id + " " + cc.body);
    }

    public List<MockReturnClass> get() {
        List<MockReturnClass> objects = new ArrayList<>();
        objects.add(new MockReturnClass("a", "b"));
        objects.add(new MockReturnClass("c", "d"));
        objects.add(new MockReturnClass("e", "f"));
        return objects;
    }

    public MockReturnClass get1(String p1) {
        return new MockReturnClass(p1, p1);
    }

    public MockReturnClass get1B(String s) {
        return new MockReturnClass(s, "B");
    }

    public RDFStream register(RDFStream rdfStream) {
        return null;
    }
}
