package it.polimi.sr.wasp.test.vocals;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.sr.wasp.server.Server;
import it.polimi.sr.wasp.server.handlers.AbstractReflectiveRequestHandler;
import it.polimi.sr.wasp.server.model.Answer;
import it.polimi.sr.wasp.model.TestStream;
import it.polimi.sr.wasp.test.vocals.model.MockDouble;
import lombok.AllArgsConstructor;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ServerTest extends Server {

    MockDouble engine = new MockDouble("csparql", "http://localhost");

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

        assertEquals(new Answer(200, new TestStream("testTask", "testTask")), process);

        Answer bad = getRequestHandler.process(engine, 1);

        assertEquals(400, bad.getCode());

    }

    @Test
    public void post() throws NoSuchMethodException {
        Method method = engine.getClass().getMethod("register_stream", String.class, TaskStub.class);
        Endpoint.Par[] params = new Endpoint.Par[]{
                new Endpoint.Par("uri_param1", 0, true),
                new Endpoint.Par("body_param2", 1, false)};

        String mockFeature = "QueryRegistrationFeature";
        String uri = "/custompostmethod";
        Endpoint endpoint = new Endpoint(mockFeature, uri, HttpMethod.GET, mockFeature, params);

        AbstractReflectiveRequestHandler.PostRequestHandler requestHandler =
                new AbstractReflectiveRequestHandler.PostRequestHandler(engine, endpoint, method);

        TaskStub q = new TaskStub("q1", "Body", "out", new String[]{"str1", "str2"});
        Answer process = requestHandler.process(engine, "stream", q);

        assertEquals(new Answer(200, new TaskStub(q.id, q.body, q.output_stream, q.input_streams)), process);

    }

    @Override
    protected void ingnite(String host, String path, int port) {

    }

    @AllArgsConstructor
    private class TaskStub {
        String id, body, output_stream;
        String[] input_streams;
    }
}
