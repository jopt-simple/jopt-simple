package joptsimple.examples;

import static java.util.Arrays.*;
import static java.util.Collections.*;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongOptionsWithArgumentsTest {
    @Test
    public void supportsLongOptionsWithArgumentsAndAbbreviations() {
        OptionParser parser = new OptionParser();
        parser.accepts( "flag" );
        parser.accepts( "count" ).withRequiredArg();
        parser.accepts( "level" ).withOptionalArg();

        OptionSet options = parser.parse( "-flag", "--co", "3", "--lev" );

        assertTrue( options.has( "flag" ) );

        assertTrue( options.has( "count" ) );
        assertTrue( options.hasArgument( "count" ) );
        assertEquals( "3", options.valueOf( "count" ) );
        assertEquals( asList( "3" ), options.valuesOf( "count" ) );

        assertTrue( options.has( "level" ) );
        assertFalse( options.hasArgument( "level" ) );
        assertNull( options.valueOf( "level" ) );
        assertEquals( emptyList(), options.valuesOf( "level" ) );
    }

    @Test
    public void supportsLongOptionsWithEmptyArguments() {
        OptionParser parser = new OptionParser();
        parser.accepts( "verbose" );
        parser.accepts( "brief" );
        parser.accepts( "add" );
        parser.accepts( "append" );
        parser.accepts( "delete" ).withRequiredArg();
        parser.accepts( "create" ).withRequiredArg();
        parser.accepts( "file" ).withRequiredArg();

        OptionSet options = parser.parse( "--delete", "", "--add" );

        assertTrue( options.has( "delete" ) );
        assertTrue( options.hasArgument( "delete" ) );
        assertEquals( "", options.valueOf( "delete" ) );

        assertTrue( options.has( "add" ) );

        options = parser.parse( "--delete=", "--add" );

        assertTrue( options.has( "delete" ) );
        assertTrue( options.hasArgument( "delete" ) );
        assertEquals( "", options.valueOf( "delete" ) );

        assertTrue( options.has( "add" ) );
    }
}
