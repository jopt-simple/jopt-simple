package joptsimple.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.junit.Test;

public class ShortOptionsClusteringWithArgumentTest {
    @Test
    public void allowsClusteringShortOptionsThatAcceptArguments() {
        OptionParser parser = new OptionParser();
        parser.accepts("a");
        parser.accepts("B");
        parser.accepts("c").withRequiredArg();

        OptionSet options = parser.parse( "-aBcfoo" );

        assertTrue( options.has( "a" ) );
        assertTrue( options.has( "B" ) );
        assertTrue( options.has( "c" ) );
        assertEquals( "foo", options.valueOf("c"));
    }
}
