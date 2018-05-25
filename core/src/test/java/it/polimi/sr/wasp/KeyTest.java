package it.polimi.sr.wasp;

import it.polimi.sr.wasp.server.model.persist.Key;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.model.TestStream;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;

public class KeyTest {

    @Test
    public void subkeys() {

        Map<Key, Channel> streams = new HashMap<>();

        Channel s = new TestStream("s1", "s1");
        Channel s2 = new TestStream("s2", "s2");

        Key k = KeyFactory.create(s.iri());

        streams.put(k, s);

        Key sk1 = KeyFactory.create(k);

        assertTrue(streams.containsKey(k));

        assertNotSame(k, sk1);

        assertEquals(s, streams.get(k));

        assertEquals(s2, streams.computeIfAbsent(sk1, key -> s2));
        assertTrue(streams.containsKey(sk1));

        assertEquals("{ \"key\": \"s1\", \"class\": \"String\"}", k.toString());
        assertEquals("{ \"key\": \"{ \"key\": \"s1\", \"class\": \"String\"}\", \"class\": \"Key\"}", sk1.toString());
    }
}
