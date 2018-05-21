package it.polimi.rsp.server.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Status {

    public static final Map<Key, Source> sources = new HashMap<>();
    public static final Map<Key, Sink> sinks = new HashMap<>();
    public static final Map<Key, Proxy> proxies = new HashMap<>();
    public static final Map<Key, Task> tasks = new HashMap<>();

    static {
        instance = new Status();
    }

    private static Status instance;

    public static Status get() {
        return instance;
    }

    public static Optional<Sink> getSink(Key k) {
        return Optional.ofNullable(sinks.get(k));

    }

    public static Optional<Source> getSource(Key k) {
        return Optional.ofNullable(sources.get(k));

    }

    public static Optional<Proxy> getProxy(Key k) {
        return Optional.ofNullable(proxies.get(k));

    }

    public static void addSink(Sink invoke) {

    }

    public static void addSource(Source invoke) {

    }

    public static void addTask(Task invoke) {

    }

    public static void addProxy(Proxy invoke) {

    }

    public static void add(Object o) {
        if (o instanceof Sink)
            addSink((Sink) o);
        if (o instanceof Source)
            addSource((Source) o);
        if (o instanceof Task)
            addTask((Task) o);
        if (o instanceof Proxy)
            addProxy((Proxy) o);

    }
}

