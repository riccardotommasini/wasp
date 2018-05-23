package it.polimi.rsp;

import it.polimi.rsp.server.enums.Protocols;
import it.polimi.rsp.server.exceptions.DuplicateException;
import it.polimi.rsp.server.exceptions.ResourceNotFound;
import it.polimi.rsp.server.model.Key;
import it.polimi.rsp.server.model.KeyFactory;
import it.polimi.rsp.server.model.StatusManager;
import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.server.web.Proxy;
import it.polimi.rsp.server.web.Task;
import it.polimi.rsp.test.mock.EmptyTask;
import org.junit.Test;

import java.util.Observer;

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
        Proxy p = new Proxy() {

            Stream s = new Stream() {
                @Override
                public String iri() {
                    return null;
                }

                @Override
                public void message(String task) {

                }

                @Override
                public void observer(Observer observer) {

                }
            };

            @Override
            public Stream stream() {
                return s;
            }

            @Override
            public void message(Object msg) {

            }

            @Override
            public void task(Task t) {

            }

            @Override
            public void stop() {

            }
        };
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
}
