package it.polimi.test;

import it.polimi.deib.rsp.vocals.rdf4j.VocalsFactoryRDF4J;
import it.polimi.sr.wasp.rsp.RSPServer;
import it.polimi.sr.wasp.rsp.exceptions.InternalEngineException;
import it.polimi.sr.wasp.rsp.model.InternalTaskWrapper;
import it.polimi.sr.wasp.rsp.model.StatelessDataChannel;
import it.polimi.sr.wasp.rsp.model.TaskBody;
import it.polimi.sr.wasp.rsp.processor.RSPEngine;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.utils.Config;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.List;

import static it.polimi.sr.wasp.utils.URIUtils.getQueryUri;
import static it.polimi.sr.wasp.utils.URIUtils.getStreamUri;

@Log
public class FakeEngine extends RSPEngine {

    public FakeEngine(String name, String base) {
        super(name, base);
    }

    @Override
    protected String[] extractStreams(TaskBody body) {
        return new String[0];
    }

    @Override
    protected InternalTaskWrapper handleInternalQuery(TaskBody tb, List<Channel> streams) throws InternalEngineException {
        String qid = getQueryUri(base, tb.id);
        String body = tb.body;
        String uri = getStreamUri(base, tb.id);
        String source = tb.id;
        String tbox = tb.tbox;
        String format = tb.format;
        return new FakeQuery(qid, body, uri, source);
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

        public static void main(String[] args) throws IOException, ClassNotFoundException {
            if (args.length > 0) {

                Config.initialize(args[0]);
                Config config = Config.getInstance();
                int port = config.getServerPort();
                String host = config.getHostName();
                String name = config.getServerName();

                RSPEngine csparql = new FakeEngine(name, "http://" + host + ":" + port);
                new Main().start(csparql, config);
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
