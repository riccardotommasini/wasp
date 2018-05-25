package it.polimi.sr.wasp.server.model.persist;

import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.model.concept.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.*;


@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatusManager {

    public static final Map<Key, Channel> streams = new HashMap<>();
    public static final Map<Key, Task> tasks = new HashMap<>();
    public static final Map<Key, Source> sources = new HashMap<>();
    public static final Map<Key, Sink> sinks = new HashMap<>();

    static {
        instance = new StatusManager();
    }

    private static StatusManager instance;

    public static StatusManager get() {
        return instance;
    }

    public static Optional<Channel> getStream(Key k) {
        Channel value = streams.get(k);
        return Optional.ofNullable(value);
    }

    public static Optional<Task> getTask(Key k) {
        return Optional.ofNullable(tasks.get(k));
    }


    public static List<Object> commit(Key k, Object... os) throws DuplicateException {
        List<Object> oos = new ArrayList<>();
        for (Object o : os) {
            oos.add(commit(k, o));
        }
        return oos;
    }

    public static Object commit(Key key, Object o) throws DuplicateException {

        if (o instanceof Source) {
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

        if (o instanceof Channel) {
            checkDuplicate(streams, key);
            streams.put(key, (Channel) o);
        }


        return o;
    }

    private static void checkDuplicate(Map m, Key key) throws DuplicateException {
        if (m.containsKey(key))
            throw new DuplicateException(key.toString());
    }

    public static void remove(Key key) throws ResourceNotFound {
        boolean test = false;
        Object removed = null;
        if (sources.containsKey(key)) {
            test = true;
            removed = sources.remove(key);
        }
        if (sinks.containsKey(key)) {
            test = true;
            removed = sinks.remove(key);
        }
        if (streams.containsKey(key)) {
            test = true;
            removed = streams.remove(key);
        }
        if (tasks.containsKey(key)) {
            test = true;
            removed = tasks.remove(key);
        }

        if (removed instanceof Stoppable)
            ((Stoppable) removed).stop();

        if (!test)
            throw new ResourceNotFound(key.toString());
    }

    public static void clear() {
        streams.clear();
        sources.clear();
        sinks.clear();
        tasks.clear();
    }
}

