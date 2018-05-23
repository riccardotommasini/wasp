package it.polimi.rsp.test.vocals;

import it.polimi.rsp.server.HttpMethod;
import it.polimi.rsp.server.Server;
import it.polimi.rsp.server.handlers.AbstractReflectiveRequestHandler;
import it.polimi.rsp.server.model.Answer;
import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.test.mock.InStream;
import it.polimi.rsp.test.mock.MockEngine;
import it.polimi.rsp.test.mock.QueryBody;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ServerTest extends Server {

    MockEngine engine = new MockEngine("csparql", "http://localhost");

    public static void main(String[] args) {
        new ServerTest().start(new MockEngine("csparql", "http://localhost"), "/Users/riccardo/_Projects/RSP/rspservices2/src/testTask/resources/testconfig.properties");

    }

    @Test
    public void get() throws NoSuchMethodException {
        Method method = engine.getClass().getMethod("customGetMethod", String.class);
        Endpoint.Par[] params = new Endpoint.Par[]{
                new Endpoint.Par("uri_param1", 0, true)};

        String mockFeature = "MockFeatureGet";
        String uri = "/customgetmethod";
        Endpoint endpoint = new Endpoint(mockFeature, uri, HttpMethod.GET, mockFeature, params);

        AbstractReflectiveRequestHandler.GetRequestHandler getRequestHandler = new AbstractReflectiveRequestHandler.GetRequestHandler(engine, endpoint, method);

        Answer process = getRequestHandler.process(engine, "testTask");

        assertEquals(new Answer(200, new InStream("testTask", "testTask")), process);

        Answer bad = getRequestHandler.process(engine, 1);

        assertEquals(400, bad.getCode());

    }

    @Test
    public void post() throws NoSuchMethodException {
        Method method = engine.getClass().getMethod("register_stream", String.class, QueryBody.class);
        Endpoint.Par[] params = new Endpoint.Par[]{
                new Endpoint.Par("uri_param1", 0, true),
                new Endpoint.Par("body_param2", 1, false)};

        String mockFeature = "QueryRegistrationFeature";
        String uri = "/custompostmethod";
        Endpoint endpoint = new Endpoint(mockFeature, uri, HttpMethod.GET, mockFeature, params);

        AbstractReflectiveRequestHandler.PostRequestHandler requestHandler =
                new AbstractReflectiveRequestHandler.PostRequestHandler(engine, endpoint, method);

        QueryBody q = new QueryBody("q1", "Body", "out", new String[]{"str1", "str2"});
        Answer process = requestHandler.process(engine, "stream", q);

        assertEquals(new Answer(200, new QueryBody(q.id, q.body, q.output_stream, q.input_streams)), process);

    }

}
