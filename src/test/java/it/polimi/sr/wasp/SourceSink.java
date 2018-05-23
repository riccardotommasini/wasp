package it.polimi.sr.wasp;

import it.polimi.sr.wasp.server.model.QueryStream;
import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.server.web.*;
import it.polimi.sr.wasp.test.mock.model.InStream;
import it.polimi.sr.wasp.test.mock.model.Query;
import org.junit.Test;
import spark.Service;

import static spark.Spark.init;
import static spark.Spark.port;

public class SourceSink {

    @Test
    public static void test() {

        Task q1 = new Query();

        Stream s = new QueryStream("s1", q1);
        Proxy proxy = SourceSinkFactory.internal(s);
        proxy.task(q1);
        Task o = new FlushStreamTask(s);
        o.sink(proxy);

    }

    public static void main(String[] args) {

        Service ws = Service.ignite().port(4001);

        Stream s = new InStream("s1", "ws://localhost:4000/echo");

        Source source = SourceSinkFactory.webSocket(s);

        Task t = new FeedStreamTask(s);
        source.task(t);

        Stream s2 = new InStream("s2", "ws://localhost:4000/echo");

        Source source2 = SourceSinkFactory.webSocket(s2);

        Task t2 = new FeedStreamTask(s2);
        source2.task(t2);

        //or sink.task(t);

        Sink sink = new WebSocketSink("/s1/observer/t1", "ws://localhost:4001/s1/observer/t1", ws);

        Task o = new FlushStreamTask(s);
        o.sink(sink);

        port(8181);
        ((WebSocketSink) sink).call();
        init();
    }

}
