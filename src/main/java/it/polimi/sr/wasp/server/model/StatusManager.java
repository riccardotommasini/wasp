package it.polimi.sr.wasp.server.model;

import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.web.Proxy;
import it.polimi.sr.wasp.server.web.Sink;
import it.polimi.sr.wasp.server.web.Source;
import it.polimi.sr.wasp.server.web.Task;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.*;


@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatusManager {

    public static final Map<Key, Stream> streams = new HashMap<>();
    public static final Map<Key, Task> tasks = new HashMap<>();
    public static final Map<Key, Proxy> proxies = new HashMap<>();
    public static final Map<Key, Source> sources = new HashMap<>();
    public static final Map<Key, Sink> sinks = new HashMap<>();

    static {
        instance = new StatusManager();
    }

    private static StatusManager instance;

    public static StatusManager get() {
        return instance;
    }

    public static Optional<Stream> getStream(Key k) {
        Stream value = streams.get(k);
        return Optional.ofNullable(value);
    }

    public static Optional<Task> getTask(Key k) {
        return Optional.ofNullable(tasks.get(k));
    }

    public static Optional<Proxy> getProxy(Key k) {
        return Optional.ofNullable(proxies.get(k));
    }

    public static Optional<Proxy> getSource(Key k) {
        return Optional.ofNullable(proxies.get(k));
    }

    public static Optional<Proxy> getSink(Key k) {
        return Optional.ofNullable(proxies.get(k));
    }

    public static List<Object> commit(Key k, Object... os) throws DuplicateException {
        List<Object> oos = new ArrayList<>();
        for (Object o : os) {
            oos.add(commit(k, o));
        }
        return oos;
    }

    public static Object commit(Key key, Object o) throws DuplicateException {

        if (o instanceof Proxy) {
            checkDuplicate(proxies, key);
            Proxy proxy = (Proxy) o;
            Key key1 = KeyFactory.create(((Proxy) o).stream());
            proxies.put(key, proxy);
            sinks.put(key1, (Sink) o);
            sources.put(key1, (Source) o);
        } else if (o instanceof Source) {
            checkDuplicate(sources, key);
            sources.put(key, (Source) o);
        } else if (o instanceof Sink) {
            checkDuplicate(sinks, key);
            sinks.put(key, (Sink) o);
        }

        if (o instanceof Task) {
            checkDuplicate(tasks, key);
            tasks.put(key, (Task) o);
        }

        if (o instanceof Stream) {
            checkDuplicate(streams, key);
            streams.put(key, (Stream) o);
        }


        return o;
    }

    private static void checkDuplicate(Map m, Key key) throws DuplicateException {
        if (m.containsKey(key))
            throw new DuplicateException(key.toString());
    }

    public static void remove(Key key) throws ResourceNotFound {
        boolean test = false;
        if (sources.containsKey(key)) {
            test = true;
            sources.remove(key).stop();

        }
        if (sinks.containsKey(key)) {
            test = true;
            sinks.remove(key);
        }
        if (proxies.containsKey(key)) {
            test = true;
            proxies.remove(key);
        }

        if (streams.containsKey(key)) {
            test = true;
            streams.remove(key);
        }
        if (tasks.containsKey(key)) {
            test = true;
            tasks.remove(key);
        }

        if (!test)
            throw new ResourceNotFound(key.toString());
    }

}

