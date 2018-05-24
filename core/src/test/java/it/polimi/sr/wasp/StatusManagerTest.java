package it.polimi.sr.wasp;

import it.polimi.sr.wasp.server.enums.Protocols;
import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.model.Key;
import it.polimi.sr.wasp.server.model.KeyFactory;
import it.polimi.sr.wasp.server.model.StatusManager;
import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.server.web.Proxy;
import it.polimi.sr.wasp.server.web.SourceTask;
import org.junit.Test;

import static org.junit.Assert.*;

public class StatusManagerTest {

    public static void main(String[] args) {

        Protocols.valueOf("HTTP");
    }

    @Test
    public void testkeyFactory() {
        //Check whether the streams are registered

        Object o = new Object();

        Key key = KeyFactory.create(o, Object.class);
        Key key1 = KeyFactory.create(o, Object.class);

        assertEquals(key, key1);
        assertEquals(1, KeyFactory.size());
    }

    @Test
    public void testTask() throws DuplicateException, ResourceNotFound {
        //Check whether the streams are registered

        //Register the query to the engine
        EmptyTask queryTask = new EmptyTask();

        Key key = KeyFactory.create(queryTask);
        StatusManager.commit(key, queryTask);

        assertEquals(1, StatusManager.tasks.size());
        assertTrue(StatusManager.tasks.containsKey(key));

        StatusManager.remove(key);
        assertEquals(0, StatusManager.tasks.size());
        assertFalse(StatusManager.tasks.containsKey(key));

    }


    @Test
    public void testProxy() throws DuplicateException, ResourceNotFound {
        //Check whether the streams are registered

        //Register the query to the engine
        Proxy p = new TestProxy();
        Stream s = p.stream();
        StatusManager.commit(KeyFactory.create(p), p);

        Key key = KeyFactory.create(p);
        Key keys = KeyFactory.create(s);
        assertEquals(1, StatusManager.proxies.size());
        assertTrue(StatusManager.proxies.containsKey(key));

        assertEquals(1, StatusManager.sinks.size());
        assertTrue(StatusManager.sinks.containsKey(keys));

        assertEquals(1, StatusManager.sources.size());
        assertTrue(StatusManager.sources.containsKey(keys));

        StatusManager.remove(key);

        assertEquals(0, StatusManager.proxies.size());
        assertFalse(StatusManager.proxies.containsKey(key));

    }

    private class EmptyTask {
    }

    private class TestProxy implements Proxy {
        @Override
        public Stream stream() {
            return null;
        }

        @Override
        public void message(Object msg) {

        }

        @Override
        public void task(SourceTask t) {

        }

        @Override
        public void stop() {

        }
    }
}
