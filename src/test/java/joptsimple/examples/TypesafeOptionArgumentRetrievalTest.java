package joptsimple.examples;

import java.io.File;

import static java.util.Arrays.*;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypesafeOptionArgumentRetrievalTest {
    @Test
    public void allowsTypesafeRetrievalOfOptionArguments() {
        OptionParser parser = new OptionParser();
        OptionSpec<Integer> count = parser.accepts( "count" ).withRequiredArg().ofType( Integer.class );
        OptionSpec<File> file = parser.accepts( "file" ).withOptionalArg().ofType( File.class );
        OptionSpec<Void> verbose = parser.accepts( "verbose" );

        OptionSet options = parser.parse( "--count", "3", "--file", "/tmp", "--verbose" );

        assertTrue( options.has( verbose ) );

        assertTrue( options.has( count ) );
        assertTrue( options.hasArgument( count ) );
        Integer expectedCount = 3;
        assertEquals( expectedCount, options.valueOf( count ) );
        assertEquals( expectedCount, count.value( options ) );
        assertEquals( asList( expectedCount ), options.valuesOf( count ) );
        assertEquals( asList( expectedCount ), count.values( options ) );

        assertTrue( options.has( file ) );
        assertTrue( options.hasArgument( file ) );
        File expectedFile = new File( "/tmp" );
        assertEquals( expectedFile, options.valueOf( file ) );
        assertEquals( expectedFile, file.value( options ) );
        assertEquals( asList( expectedFile ), options.valuesOf( file ) );
        assertEquals( asList( expectedFile ), file.values( options ) );
    }
}
