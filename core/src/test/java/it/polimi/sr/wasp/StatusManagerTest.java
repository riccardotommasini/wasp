package it.polimi.sr.wasp;

import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.concept.tasks.AsynchTask;
import it.polimi.sr.wasp.server.model.concept.tasks.SynchTask;
import it.polimi.sr.wasp.server.model.concept.tasks.Task;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import it.polimi.sr.wasp.server.model.persist.Key;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyException;

import static org.junit.Assert.*;

public class StatusManagerTest {

    public StatusManagerTest() {
    }

    @Before
    public void before() {
        StatusManager.clear();
        KeyFactory.clear();
    }

    @Test
    public void testkeyFactory() {
        //Check whether the channels are registered

        Object o = new Object();

        Key key = KeyFactory.create(o, Object.class);
        Key key1 = KeyFactory.create(o, Object.class);

        assertEquals(key, key1);
        assertEquals(1, KeyFactory.size());
    }

    @Test
    public void testTask() throws DuplicateException, ResourceNotFound, KeyException {
        //Check whether the channels are registered

        //Register the query to the engine
        Task queryTask = new TestTask();
        Channel out = new TestChannel();
        Sink sink1 = new TeskSink();
        Sink sink2 = new TeskSink();

        Key query_key = KeyFactory.create(queryTask);
        StatusManager.commit(query_key, queryTask);

        Key key_out = KeyFactory.create(query_key);
        StatusManager.commit(key_out, out);

        Key sink1_key = KeyFactory.create(key_out);
        StatusManager.commit(sink1_key, sink1);

        Key sink2_key = KeyFactory.create(sink1_key);
        StatusManager.commit(sink2_key, sink2);

        assertEquals(1, StatusManager.tasks.size());
        assertTrue(StatusManager.tasks.containsKey(query_key));

        assertEquals(1, StatusManager.channels.size());
        assertTrue(StatusManager.channels.containsKey(key_out));

        assertTrue(StatusManager.sinks.containsKey(sink1_key));
        assertEquals(2, StatusManager.sinks.size());
        assertTrue(StatusManager.sinks.containsKey(sink1_key));

        assertTrue(StatusManager.sinks.containsKey(sink2_key));
        assertEquals(2, StatusManager.sinks.size());
        assertTrue(StatusManager.sinks.containsKey(sink2_key));

        StatusManager.remove(query_key);
        assertEquals(0, StatusManager.tasks.size());
        assertFalse(StatusManager.tasks.containsKey(query_key));

        assertEquals(0, StatusManager.channels.size());
        assertFalse(StatusManager.channels.containsKey(key_out));

        assertFalse(StatusManager.sinks.containsKey(sink1_key));
        assertEquals(0, StatusManager.sinks.size());
        assertFalse(StatusManager.sinks.containsKey(sink1_key));

        assertFalse(StatusManager.sinks.containsKey(sink2_key));
        assertEquals(0, StatusManager.sinks.size());
        assertFalse(StatusManager.sinks.containsKey(sink2_key));

    }


    private class TestTask implements Task {

        @Override
        public String iri() {
            return null;
        }

        @Override
        public Channel out() {
            return null;
        }

        @Override
        public Channel[] in() {
            return new Channel[0];
        }

    }

    private class TestChannel implements Channel {
        @Override
        public String iri() {
            return null;
        }

        @Override
        public Channel put(Object m) {
            return null;
        }

        @Override
        public Channel add(Sink s) {
            return null;
        }

        @Override
        public Channel add(Channel c) {
            return null;
        }

        @Override
        public Channel add(Task t) {
            return null;
        }

        @Override
        public Channel add(AsynchTask t) {
            return null;
        }

        @Override
        public Channel add(SynchTask t) {
            return null;
        }

        @Override
        public Descriptor describe() {
            return null;
        }
    }

    private class TeskSink implements Sink {
        @Override
        public Descriptor describe() {
            return null;
        }

        @Override
        public void await(String m) {

        }
    }
}
