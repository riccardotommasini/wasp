package it.polimi.sr.wasp.server.model.persist;

import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.exceptions.ResourceNotFound;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.concept.Source;
import it.polimi.sr.wasp.server.model.concept.tasks.Task;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.security.KeyException;
import java.util.*;


@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatusManager {

    public static final Map<Key, Channel> channels = new HashMap<>();
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

    public static Optional<Channel> getChannel(Key k) {
        Channel value = channels.get(k);
        return Optional.ofNullable(value);
    }

    public static Optional<Sink> getSink(Key k) {
        Sink value = sinks.get(k);
        return Optional.ofNullable(value);
    }

    public static Optional<Source> getSource(Key k) {
        Source value = sources.get(k);
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

    public static Key remove_dependent(Key k) throws ResourceNotFound, KeyException {
        for (int i = 0; i < k.hl(); i++) {
            Key key = KeyFactory.get(k);
            if (key != null) {
                if (key.hl() > 0) {
                    remove_dependent(key);
                    StatusManager.remove(KeyFactory.remove(key));
                }
            }
        }
        return k;
    }

    public static <T> T commit(Key key, T o) throws DuplicateException {

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
            checkDuplicate(channels, key);
            channels.put(key, (Channel) o);
        }


        return o;
    }

    private static void checkDuplicate(Map m, Key key) throws DuplicateException {
        if (m.containsKey(key))
            throw new DuplicateException(key.toString());
    }

    public static boolean exists(Key key) throws DuplicateException {
        return sources.containsKey(key) || sinks.containsKey(key)
                || channels.containsKey(key) || tasks.containsKey(key);
    }

    public static void remove(Key key) throws ResourceNotFound, KeyException {
        boolean test = false;
        if (sources.containsKey(key)) {
            test = true;
            Source remove = sources.remove(key);
            remove.stop();
        }
        if (sinks.containsKey(key)) {
            test = true;
            sinks.remove(key);
            if (key.hl() > 0) {
                //There is a dependent sink
                remove(KeyFactory.get(key));
            }
        }
        if (channels.containsKey(key)) {
            test = true;
            channels.remove(key);
            //check if there is any dependent sink and remove it
            if (sinks.containsKey(KeyFactory.get(key))) {
                remove(KeyFactory.get(key));
            }
        }
        if (tasks.containsKey(key)) {
            test = true;
            tasks.remove(key);
            //remove channel out
            remove(KeyFactory.get(key));
        }

        if (!test)
            throw new ResourceNotFound(key.toString());

        KeyFactory.remove(key);
    }

    public static void clear() {
        channels.clear();
        sources.clear();
        sinks.clear();
        tasks.clear();
    }
}

