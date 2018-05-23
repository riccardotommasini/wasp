package it.polimi.rsp;

import it.polimi.rsp.server.model.Key;
import it.polimi.rsp.server.model.KeyFactory;
import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.test.mock.model.InStream;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;

public class KeyTest {

    @Test
    public void subkeys() {

        Map<Key, Stream> streams = new HashMap<>();

        Stream s = new InStream("s1", "s1");
        Stream s2 = new InStream("s2", "s2");

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
