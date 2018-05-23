package it.polimi.rsp.server.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.KeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KeyFactory {

    private static final Map<Object, Key> keys = new HashMap<>();

    public static int size() {
        return keys.size();
    }

    private static Key gen(Class<?> c) {
        return create(RandomStringUtils.randomAlphabetic(10), c);
    }

    private static Key gen(Object o) {
        return gen(o.getClass());
    }

    public static Key get(Object o) {
        return keys.get(o);
    }

    public static Key create(String o) {
        return keys.computeIfAbsent(o, a -> new KeyImpl(o));
    }

    public static Key create(Key o) {
        o.hlplus();
        return create(o, Key.class);
    }

    public static Key create(Stream o) {
        return create(o.iri());
    }

    public static Key create(Object o, Class<?> c) {
        return keys.computeIfAbsent(o, a -> new KeyImpl(o, c));
    }

    public static Key create(Object o) {
        return keys.computeIfAbsent(o, a -> gen(o));
    }

    public static Key create2(Object o) {
        return keys.computeIfAbsent(o, a ->
                Arrays.stream(o.getClass().getFields())
                        .filter(field -> field.isAnnotationPresent(it.polimi.rsp.vocals.annotations.model.Key.class))
                        .map(field -> {
                            try {
                                return field.get(o);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            return o;
                        }).map(o1 -> KeyFactory.create(o, o.getClass())).findAny().orElse(KeyFactory.create(o, o.getClass()))
        );
    }

    public static Key remove(Object s) throws KeyException {
        if(s == null) {
            throw new KeyException("null key");
        }

        return keys.remove(s);
    }

    private static class KeyImpl implements Key {

        private final Object key;
        private final Class c;
        protected int hl = 0;

        public KeyImpl(Object key, Class c) {
            this.key = key;
            this.c = c;
        }

        public KeyImpl(Object key) {
            this.key = key;
            this.c = key.getClass();
        }

        @Override
        public String toString() {
            return "{ \"key\": \"" + key.toString() +
                    "\", \"class\": \"" + c.getSimpleName() +
                    "\"}";
        }

        @Override
        public int hlmin() {
            return hl--;
        }

        @Override
        public int hlplus() {
            return hl++;
        }

        @Override
        public int hl() {
            return hl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            KeyImpl key1 = (KeyImpl) o;

            if (key != null ? !key.equals(key1.key) : key1.key != null) return false;
            return c != null ? c.equals(key1.c) : key1.c == null;
        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (c != null ? c.hashCode() : 0);
            return result;
        }
    }
}
