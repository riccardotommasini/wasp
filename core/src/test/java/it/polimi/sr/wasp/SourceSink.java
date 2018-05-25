package it.polimi.sr.wasp;

import it.polimi.sr.wasp.model.TestStream;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Source;
import it.polimi.sr.wasp.server.web.WebSocketSource;
import spark.Service;

public class SourceSink {

    public static void main(String[] args) {

        Service ws = Service.ignite().port(4001);

        Channel s = new TestStream("s1", "ws://localhost:4000/echo");


    }


}
