package it.polimi.test;

import it.polimi.deib.rsp.vocals.rdf4j.VocalsFactoryRDF4J;
import it.polimi.sr.wasp.rsp.RSPEngine;
import it.polimi.sr.wasp.rsp.RSPServer;
import it.polimi.sr.wasp.rsp.exceptions.InternalEngineException;
import it.polimi.sr.wasp.rsp.model.StatelessDataChannel;
import it.polimi.sr.wasp.rsp.model.InternalTaskWrapper;
import it.polimi.sr.wasp.rsp.model.QueryBody;
import it.polimi.sr.wasp.server.model.concept.Channel;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.List;

@Log
public class FakeEngine extends RSPEngine {

    public FakeEngine(String name, String base) {
        super(name, base);
    }

    @Override
    protected String[] extractStreams(QueryBody body) {
        return new String[0];
    }

    @Override
    protected InternalTaskWrapper handleInternalQuery(String queryUri, String body, String uri, String source, List<Channel> streams) throws InternalEngineException {
        return new FakeQuery(queryUri, body, uri, source);
    }

    @Override
    protected Channel handleInternalStream(String id, String body) {
        return new StatelessDataChannel(base, id, body);
    }


    @Log
    private static class Main extends RSPServer {

        public Main() throws IOException {
            super(new VocalsFactoryRDF4J());
        }

        public static void main(String[] args) throws IOException {
            if (args.length > 0) {
                RSPEngine csparql = new FakeEngine("fake", "http://localhost:8181");
                new Main().start(csparql, args[0]);
            }
        }
    }

    private class FakeQuery extends InternalTaskWrapper {
        public FakeQuery(String queryUri, String body, String uri, String source) {
            super(queryUri, body, FakeEngine.this.base);
            this.out = new StatelessDataChannel(base, uri, source);
        }
    }
}
