package joptsimple.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>An {@code OptionNameMap} which wraps a and behaves like {@code HashMap}.</p>
 */
public class SimpleOptionNameMap<V> implements OptionNameMap<V>{

    private final Map<String, V> map = new HashMap<>();

    @Override
    public boolean contains(String aKey) {
        return map.containsKey(aKey);
    }

    @Override
    public V get(String aKey) {
        return map.get(aKey);
    }

    @Override
    public void put(String aKey, V newValue) {
        map.put(aKey, newValue);
    }

    @Override
    public void putAll(Iterable<String> keys, V newValue) {
        for( String key : keys)
            map.put(key, newValue);
    }

    @Override
    public void remove(String aKey) {
        map.remove(aKey);
    }

    @Override
    public Map<String, V> toJavaUtilMap() {
        return new HashMap<>(map);
    }
}
