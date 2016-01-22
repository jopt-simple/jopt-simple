package joptsimple.internal;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SimpleOptionNameMapTest {
    private static final Integer VALUE = 1;
    private static final String KEY = "someKey";
    private static final String KEY2 = "someOtherKey";

    @Test
    public void testPutAndContains(){
        final SimpleOptionNameMap<Integer> map = new SimpleOptionNameMap<>();
        assertFalse(map.contains(KEY));
        map.put(KEY, 1);
        assertTrue(map.contains(KEY));

    }

    @Test
    public void testGet() throws Exception {
        final SimpleOptionNameMap<Integer> map = new SimpleOptionNameMap<>();
        assertNull(map.get(KEY));
        map.put(KEY, VALUE);
        assertEquals(map.get(KEY), VALUE);
    }

    @Test
    public void testPutAll() throws Exception {
        final SimpleOptionNameMap<Integer> map = new SimpleOptionNameMap<>();
        List<String> keys = Arrays.asList(KEY, KEY2);
        map.putAll(keys, VALUE);
        assertEquals(map.get(KEY), VALUE);
        assertEquals(map.get(KEY2), VALUE);
    }

    @Test
    public void testRemove() throws Exception {
        final SimpleOptionNameMap<Integer> map = new SimpleOptionNameMap<>();
        map.put(KEY, 1);
        assertTrue(map.contains(KEY));
        map.remove(KEY);
        assertFalse(map.contains(KEY));
    }

    @Test
    public void testToJavaUtilMap() throws Exception {
        final SimpleOptionNameMap<Integer> map = new SimpleOptionNameMap<>();
        map.put(KEY, VALUE);

        final Map<String, Integer> javaUtilMap = map.toJavaUtilMap();
        assertEquals(javaUtilMap.get(KEY), VALUE);
        assertEquals(javaUtilMap.size(), 1);

    }
}
