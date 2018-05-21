package it.polimi.rsp.server.model;

import lombok.AllArgsConstructor;

public class KeyFactory {

    public static Key create(String key) {
        return new StringKey(key);
    }

    @AllArgsConstructor
    private static class StringKey implements Key {
        private final String key;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringKey stringKey = (StringKey) o;

            return key != null ? key.equals(stringKey.key) : stringKey.key == null;
        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }
    }


    @AllArgsConstructor
    private static class ObjectKey implements Key {
        private final Object key;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringKey stringKey = (StringKey) o;

            return key != null ? key.equals(stringKey.key) : stringKey.key == null;
        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }
    }
}
