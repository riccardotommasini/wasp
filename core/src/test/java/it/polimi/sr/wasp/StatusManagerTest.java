package it.polimi.sr.wasp;

import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Task;
import it.polimi.sr.wasp.server.model.persist.Key;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StatusManagerTest {

    public StatusManagerTest() {
    }

    @Before
    public void before(){
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
    public void testTask() throws DuplicateException, ResourceNotFound {
        //Check whether the channels are registered

        //Register the query to the engine
        Task queryTask = new TestTask();

        Key key = KeyFactory.create(queryTask);
        StatusManager.commit(key, queryTask);

        assertEquals(1, StatusManager.tasks.size());
        assertTrue(StatusManager.tasks.containsKey(key));

        StatusManager.remove(key);
        assertEquals(0, StatusManager.tasks.size());
        assertFalse(StatusManager.tasks.containsKey(key));

    }


    private class TestTask implements Task {
        @Override
        public Channel out() {
            return null;
        }

        @Override
        public Channel[] in() {
            return new Channel[0];
        }
    }
}
