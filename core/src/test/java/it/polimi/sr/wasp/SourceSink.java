package it.polimi.sr.wasp;

import it.polimi.sr.wasp.server.model.QueryStream;
import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.server.web.*;
import it.polimi.sr.wasp.model.TestStream;
import org.junit.Test;
import spark.Service;

import static spark.Spark.init;
import static spark.Spark.port;

public class SourceSink {

    @Test
    public static void test() {

        MockTask q1 = new MockTask();

        Stream s = new QueryStream("s1", q1);
        Proxy proxy = SourceSinkFactory.internal(s);
        proxy.task(q1);
        FlushStreamTask o = new FlushStreamTask(s, proxy);
        o.sink(proxy);

    }

    public static void main(String[] args) {

        Service ws = Service.ignite().port(4001);

        Stream s = new TestStream("s1", "ws://localhost:4000/echo");

        Source source = SourceSinkFactory.webSocket(s);

        SourceTask t = new FeedStreamTask(s, source);
        source.task(t);

        Stream s2 = new TestStream("s2", "ws://localhost:4000/echo");

        Source source2 = SourceSinkFactory.webSocket(s2);

        SourceTask t2 = new FeedStreamTask(s2, source2);
        source2.task(t2);

        //or sink.task(t);

        Sink sink = new WebSocketSink("/s1/observer/t1", "ws://localhost:4001/s1/observer/t1", ws);

        SinkTask o = new FlushStreamTask(s, sink);
        o.sink(sink);

        port(8181);
        ((WebSocketSink) sink).call();
        init();
    }

    private static class MockTask implements ProxyTask {
        @Override
        public String iri() {
            return null;
        }

        @Override
        public void update(String message) {

        }

        @Override
        public Stream stream() {
            return null;
        }

        @Override
        public Proxy proxy() {
            return null;
        }

        @Override
        public void sink(Sink sink) {

        }

        @Override
        public Sink sink() {
            return null;
        }

        @Override
        public void source(Source source) {

        }

        @Override
        public Source source() {
            return null;
        }
    }
}
