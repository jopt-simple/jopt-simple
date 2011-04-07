package joptsimple.examples;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.junit.Test;

public class ShortOptionsWithMultipleArgumentsForSingleOptionTest {
    @Test
    public void shouldAllowMultipleValuesForAnOption() {
        OptionParser parser = new OptionParser( "a:" );

        OptionSet options = parser.parse( "-a", "foo", "-abar", "-a=baz" );

        assertTrue( options.has( "a" ) );
        assertTrue( options.hasArgument( "a" ) );
        assertEquals( asList( "foo", "bar", "baz" ), options.valuesOf( "a" ) );

        try {
            options.valueOf( "a" );
            fail( "Should raise exception when asking for one of many args" );
        }
        catch ( OptionException expected ) {
            // success
        }
    }
}
