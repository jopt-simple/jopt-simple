package joptsimple.examples;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShortOptionsClusteringTest {
    @Test
    public void allowsClusteringShortOptions() {
        OptionParser parser = new OptionParser( "aBcd" );

        OptionSet options = parser.parse( "-cdBa" );

        assertTrue( options.has( "a" ) );
        assertTrue( options.has( "B" ) );
        assertTrue( options.has( "c" ) );
        assertTrue( options.has( "d" ) );
    }

    @Test
    public void rejectsClusteringShortOptions() {
        OptionParser parser = new OptionParser();
        parser.allowsOptionClustering(false);
        parser.accepts( "a" );
        parser.accepts( "b" );
        parser.accepts( "c" );

        OptionSet options = parser.parse( "-abcd" );
        assertFalse(options.has("a"));
        assertFalse(options.has("b"));
        assertFalse(options.has("c"));
        assertFalse(options.has("abcd"));
    }

}
