package net.sf.joptsimple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import net.sf.joptsimple.internal.AbbreviationMap;
import org.junit.jupiter.api.Test;

public class AbbreviationMapTest {
    @Test
    public void removingLongerKeyRetainsPrefixKey() {
        AbbreviationMap<String> map = new AbbreviationMap<>();
        map.put( "a", "first" );
        map.put( "ab", "second" );

        map.remove( "ab" );

        assertEquals( Map.of( "a", "first" ), map.toJavaUtilMap() );
        assertEquals( "first", map.get( "a" ) );
    }

    @Test
    public void removingLongerKeyRetainsOtherBranches() {
        AbbreviationMap<String> map = new AbbreviationMap<>();
        map.put( "a", "first" );
        map.put( "ab", "second" );
        map.put( "x", "third" );

        map.remove( "ab" );

        assertEquals( Map.of( "a", "first", "x", "third" ), map.toJavaUtilMap() );
    }

    @Test
    public void removingPrefixKeyRetainsLongerKey() {
        AbbreviationMap<String> map = new AbbreviationMap<>();
        map.put( "a", "first" );
        map.put( "ab", "second" );

        map.remove( "a" );

        assertEquals( Map.of( "ab", "second" ), map.toJavaUtilMap() );
        assertEquals( "second", map.get( "a" ) );
    }
}
