package joptsimple.examples;

import static joptsimple.examples.Level.*;
import static org.junit.Assert.*;

import java.io.File;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.junit.Test;

public class DefaultValuesForOptionArgumentsTest {
    @Test
    public void shouldAllowSpecificationOfDefaultValues() throws Exception {
        File tempDir = new File( System.getProperty( "java.io.tmpdir" ) );
        File tempFile = File.createTempFile( "aFile", ".txt" );
        OptionParser parser = new OptionParser();
        OptionSpec<File> infile =
            parser.accepts( "infile" ).withRequiredArg().ofType( File.class ).defaultsTo( tempFile );
        OptionSpec<File> outdir =
            parser.accepts( "outdir" ).withRequiredArg().ofType( File.class ).defaultsTo( tempDir );
        OptionSpec<Integer> bufferSize =
            parser.accepts( "buffer-size" ).withOptionalArg().ofType( Integer.class ).defaultsTo( 4096 );
        OptionSpec<Level> level =
            parser.accepts( "level" ).withOptionalArg().ofType( Level.class ).defaultsTo( INFO );
        OptionSpec<Integer> count =
            parser.accepts( "count" ).withOptionalArg().ofType( Integer.class ).defaultsTo( 10 );

        OptionSet options = parser.parse( "--level", "WARNING", "--count", "--infile", "/etc/passwd" );

        assertEquals( new File( "/etc/passwd" ), infile.value( options ) );
        assertTrue( options.has( infile ) );
        assertTrue( options.hasArgument( infile ) );
        assertEquals( tempDir, outdir.value( options ) );
        assertFalse( options.has( outdir ) );
        assertFalse( options.hasArgument( outdir ) );
        assertEquals( Integer.valueOf( 4096 ), bufferSize.value( options ) );
        assertFalse( options.has( bufferSize ) );
        assertFalse( options.hasArgument( bufferSize ) );
        assertEquals( WARNING, level.value( options ) );
        assertTrue( options.has( level ) );
        assertTrue( options.hasArgument( level ) );
        assertEquals( Integer.valueOf( 10 ), count.value( options ) );
        assertTrue( options.has( count ) );
        assertFalse( options.hasArgument( count ) );

        try {
            parser.parse( "--outdir" );
            fail();
        }
        catch ( OptionException expected ) {
            // because you still must specify an argument if you give the option on the command line
        }
    }
}
