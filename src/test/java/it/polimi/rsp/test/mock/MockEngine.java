package it.polimi.rsp.test.mock;

import it.polimi.rsp.vocals.annotations.Base;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
@Getter
@Base(base = "http://localhost:8182/")
public class MockEngine implements MockFeaturePost, MockFeatureGet {

    private final String base;
    private final String name;

    public MockEngine(String name, String base) {
        this.name = name;
        this.base = base;
    }

    @Override
    public MockReturnClass customPostMethod(String id, MockInputClass cc) {
        log.info(cc.id + " " + cc.body);
        return new MockReturnClass(cc.body, cc.id);
    }


    @Override
    public MockReturnClass customGetMethod(String id) {
        return new MockReturnClass(id, id);
    }
}
