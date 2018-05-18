package it.polimi.rsp.test.mock.vocals;

import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.server.Server;
import it.polimi.rsp.server.model.Answer;
import it.polimi.rsp.server.handlers.GetRequestHandler;
import it.polimi.rsp.server.HttpMethod;
import it.polimi.rsp.server.handlers.PostRequestHandler;
import it.polimi.rsp.test.mock.MockEngine;
import it.polimi.rsp.test.mock.MockInputClass;
import it.polimi.rsp.test.mock.MockReturnClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ServerTest extends Server {

    MockEngine engine = new MockEngine("csparql", "http://localhost");

    public static void main(String[] args) {
        new ServerTest().start(new MockEngine("csparql", "http://localhost"), "/Users/riccardo/_Projects/RSP/rspservices2/src/test/resources/testconfig.properties");

    }

    @Test
    public void get() throws NoSuchMethodException {
        Method method = engine.getClass().getMethod("customGetMethod", String.class);
        Endpoint.Par[] params = new Endpoint.Par[]{
                new Endpoint.Par("uri_param1", 0, true)};

        String mockFeature = "MockFeatureGet";
        String uri = "/customgetmethod";
        Endpoint endpoint = new Endpoint(mockFeature, uri, HttpMethod.GET, mockFeature, params);

        GetRequestHandler getRequestHandler = new GetRequestHandler(engine, endpoint, method);

        Answer process = getRequestHandler.process(engine, "test");

        assertEquals(new Answer(200, new MockReturnClass("test", "test")), process);

        Answer bad = getRequestHandler.process(engine, 1);

        assertEquals(400, bad.getCode());

    }

    @Test
    public void post() throws NoSuchMethodException {
        Method method = engine.getClass().getMethod("customPostMethod", String.class, MockInputClass.class);
        Endpoint.Par[] params = new Endpoint.Par[]{
                new Endpoint.Par("uri_param1", 0, true),
                new Endpoint.Par("body_param2", 1, false)};

        String mockFeature = "MockFeaturePost";
        String uri = "/custompostmethod";
        Endpoint endpoint = new Endpoint(mockFeature, uri, HttpMethod.GET, mockFeature, params);

        PostRequestHandler requestHandler = new PostRequestHandler(engine, endpoint, method);

        MockInputClass mockInputClass = new MockInputClass("Body1", "Body2");
        Answer process = requestHandler.process(engine, "test_uri", mockInputClass);

        assertEquals(new Answer(200, new MockReturnClass(mockInputClass.body, mockInputClass.id)), process);

    }

}
