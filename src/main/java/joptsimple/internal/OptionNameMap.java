package joptsimple.internal;

import java.util.Map;

/**
 * Map like interface for storing String -> V pairs
 * @param <V>
 */
public interface OptionNameMap<V> {
    boolean contains(String aKey);

    V get(String aKey);

    void put(String aKey, V newValue);

    void putAll(Iterable<String> keys, V newValue);

    void remove(String aKey);

    Map<String, V> toJavaUtilMap();
}
