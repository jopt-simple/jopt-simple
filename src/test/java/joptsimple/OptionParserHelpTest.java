/*
 The MIT License

 Copyright (c) 2004-2011 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package joptsimple;

import static java.lang.System.*;
import static java.math.BigDecimal.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static joptsimple.internal.Strings.*;
import static joptsimple.util.DateConverter.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class OptionParserHelpTest extends AbstractOptionParserFixture {
    private static final String EXPECTED_HEADER =
        "Option                                  Description                            ";
    private static final String EXPECTED_HEADER_WITH_REQUIRED_INDICATOR =
        "Option (* = required)                   Description                            ";
    private static final String EXPECTED_SEPARATOR =
        "------                                  -----------                            ";
    private static final String EXPECTED_SEPARATOR_WITH_REQUIRED_INDICATOR =
        "---------------------                   -----------                            ";

    private StringWriter sink;

    @Before
    public final void createSink() {
        sink = new StringWriter();
    }

    @Test
    public void unconfiguredParser() throws Exception {
        parser.printHelpOn( sink );

        assertEquals( "No options specified", sink.toString() );
    }

    @Test
    public void oneOptionNoArgNoDescription() throws Exception {
        parser.accepts( "apple" );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "--apple                                                                        " );
    }

    @Test
    public void oneOptionNoArgWithDescription() throws Exception {
        parser.accepts( "a", "some description" );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-a                                      some description                       " );
    }

    @Test
    public void twoOptionsNoArgWithDescription() throws Exception {
        parser.accepts( "a", "some description" );
        parser.accepts( "verbose", "even more description" );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-a                                      some description                       ",
            "--verbose                               even more description                  " );
    }

    @Test
    public void oneOptionRequiredArgNoDescription() throws Exception {
        parser.accepts( "a" ).withRequiredArg();

        parser.printHelpOn( sink );

        assertStandardHelpLines(
        "-a                                                                             " );
    }

    @Test
    public void oneOptionRequiredArgNoDescriptionWithType() throws Exception {
        parser.accepts( "a" ).withRequiredArg().ofType( Integer.class );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-a <Integer>                                                                   " );
    }

    @Test
    public void oneOptionRequiredArgWithDescription() throws Exception {
        parser.accepts( "a", "some value you need" )
            .withRequiredArg().describedAs( "numerical" );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-a <numerical>                          some value you need                    " );
    }

    @Test
    public void oneOptionRequiredArgWithDescriptionAndType() throws Exception {
        parser.accepts( "a", "some value you need" )
            .withRequiredArg().describedAs( "numerical" ).ofType( Integer.class );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-a <Integer: numerical>                 some value you need                    " );
    }

    @Test
    public void oneOptionOptionalArgNoDescription() throws Exception {
        parser.accepts( "threshold" ).withOptionalArg();

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "--threshold                                                                    " );
    }

    @Test
    public void oneOptionOptionalArgNoDescriptionWithType() throws Exception {
        parser.accepts( "a" ).withOptionalArg().ofType( Float.class );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-a [Float]                                                                     " );
    }

    @Test
    public void oneOptionOptionalArgWithDescription() throws Exception {
        parser.accepts( "threshold", "some value you need" )
            .withOptionalArg().describedAs( "positive integer" );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "--threshold [positive integer]          some value you need                    " );
    }

    @Test
    public void oneOptionOptionalArgWithDescriptionAndType() throws Exception {
        parser.accepts( "threshold", "some value you need" )
            .withOptionalArg().describedAs( "positive decimal" ).ofType( Double.class );

        parser.printHelpOn( sink );

        assertHelpLines(
            "Option                                  Description                            ",
            "------                                  -----------                            ",
            "--threshold [Double: positive decimal]  some value you need                    ",
            "" );
    }

    @Test
    public void alternativeLongOptions() throws Exception {
        parser.recognizeAlternativeLongOptions( true );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-W <opt=value>                          Alternative form of long options       " );
    }

    @Test
    public void optionSynonymsWithoutArguments() throws Exception {
        parser.acceptsAll( asList( "v", "chatty" ), "be verbose" );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-v, --chatty                            be verbose                             " );
    }

    @Test
    public void optionSynonymsWithRequiredArgument() throws Exception {
        parser.acceptsAll( asList( "L", "index" ), "set level" )
            .withRequiredArg().ofType( Integer.class );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-L, --index <Integer>                   set level                              " );
    }

    @Test
    public void optionSynonymsWithOptionalArgument() throws Exception {
        parser.acceptsAll( asList( "d", "since" ), "date filter" )
            .withOptionalArg().describedAs( "yyyyMMdd" ).ofType( Date.class );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-d, --since [Date: yyyyMMdd]            date filter                            " );
    }

    @Test
    public void optionSynonymsSortedByShortOptionThenLexicographical() throws Exception {
        parser.acceptsAll( asList( "v", "prolix", "chatty" ) );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-v, --chatty, --prolix                                                         " );
    }

    @Test
    public void writingToOutputStream() throws Exception {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

        parser.printHelpOn( bytesOut );

        assertEquals( "No options specified", bytesOut.toString() );
    }

    // Bug 1956418
    @Test
    public void outputStreamShouldBeFlushedButNotClosedWhenPrintingHelp()
        throws Exception {

        FakeOutputStream fake = new FakeOutputStream();

        parser.printHelpOn( fake );

        assertTrue( fake.flushed );
        assertFalse( fake.closed );
    }

    @Test
    public void bothColumnsExceedingAllocatedWidths() throws Exception {
        parser.acceptsAll( asList( "t", "threshold", "cutoff" ),
            "a threshold value beyond which a certain level of the application should cease to write logs" )
            .withRequiredArg()
            .describedAs( "a positive decimal number that will represent the threshold that has been outlined" )
            .ofType( Double.class );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-t, --cutoff, --threshold <Double: a    a threshold value beyond which a       ",
            "  positive decimal number that will       certain level of the application     ",
            "  represent the threshold that has        should cease to write logs           ",
            "  been outlined>                                                               " );
    }

    // Bug 2018262
    @Test
    public void gradleHelp() throws Exception {
        parser.acceptsAll( asList( "n", "non-recursive" ),
            "Do not execute primary tasks of child projects." );
        parser.acceptsAll( singletonList( "S" ),
            "Don't trigger a System.exit(0) for normal termination. Used for Gradle's internal testing." );
        parser.acceptsAll( asList( "I", "no-imports" ),
            "Disable usage of default imports for build script files." );
        parser.acceptsAll( asList( "u", "no-search-upward" ),
            "Don't search in parent folders for a settings.gradle file." );
        parser.acceptsAll( asList( "x", "cache-off" ),
            "No caching of compiled build scripts." );
        parser.acceptsAll( asList( "r", "rebuild-cache" ),
            "Rebuild the cache of compiled build scripts." );
        parser.acceptsAll( asList( "v", "version" ), "Print version info." );
        parser.acceptsAll( asList( "d", "debug" ),
            "Log in debug mode (includes normal stacktrace)." );
        parser.acceptsAll( asList( "q", "quiet" ), "Log errors only." );
        parser.acceptsAll( asList( "j", "ivy-debug" ),
            "Set Ivy log level to debug (very verbose)." );
        parser.acceptsAll( asList( "i", "ivy-quiet" ), "Set Ivy log level to quiet." );
        parser.acceptsAll( asList( "s", "stacktrace" ),
            "Print out the stacktrace also for user exceptions (e.g. compile error)." );
        parser.acceptsAll( asList( "f", "full-stacktrace" ),
            "Print out the full (very verbose) stacktrace for any exceptions." );
        parser.acceptsAll( asList( "t", "tasks" ),
            "Show list of all available tasks and their dependencies." );
        parser.acceptsAll( asList( "p", "project-dir" ),
            "Specifies the start dir for Gradle. Defaults to current dir." )
            .withRequiredArg().ofType( String.class );
        parser.acceptsAll( asList( "g", "gradle-user-home" ),
            "Specifies the gradle user home dir." )
            .withRequiredArg().ofType( String.class );
        parser.acceptsAll( asList( "l", "plugin-properties-file" ),
            "Specifies the plugin.properties file." )
            .withRequiredArg().ofType( String.class );
        parser.acceptsAll( asList( "b", "buildfile" ),
            "Specifies the build file name (also for subprojects). Defaults to build.gradle." )
            .withRequiredArg().ofType( String.class );
        parser.acceptsAll( asList( "D", "systemprop" ),
            "Set system property of the JVM (e.g. -Dmyprop=myvalue)." )
            .withRequiredArg().ofType( String.class );
        parser.acceptsAll( asList( "P", "projectprop" ),
            "Set project property for the build script (e.g. -Pmyprop=myvalue)." )
            .withRequiredArg().ofType( String.class );
        parser.acceptsAll( asList( "e", "embedded" ),
            "Specify an embedded build script." )
            .withRequiredArg().ofType( String.class );
        parser.acceptsAll( asList( "B", "bootstrap-debug" ),
            "Specify a text to be logged at the beginning (e.g. used by Gradle's bootstrap class." )
            .withRequiredArg().ofType( String.class );
        parser.acceptsAll( asList( "h", "?" ), "Shows this help message" );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-?, -h                                  Shows this help message                ",
            "-B, --bootstrap-debug                   Specify a text to be logged at the     ",
            "                                          beginning (e.g. used by Gradle's     ",
            "                                          bootstrap class.                     ",
            "-D, --systemprop                        Set system property of the JVM (e.g. - ",
            "                                          Dmyprop=myvalue).                    ",
            "-I, --no-imports                        Disable usage of default imports for   ",
            "                                          build script files.                  ",
            "-P, --projectprop                       Set project property for the build     ",
            "                                          script (e.g. -Pmyprop=myvalue).      ",
            "-S                                      Don't trigger a System.exit(0) for     ",
            "                                          normal termination. Used for         ",
            "                                          Gradle's internal testing.           ",
            "-b, --buildfile                         Specifies the build file name (also    ",
            "                                          for subprojects). Defaults to build. ",
            "                                          gradle.                              ",
            "-d, --debug                             Log in debug mode (includes normal     ",
            "                                          stacktrace).                         ",
            "-e, --embedded                          Specify an embedded build script.      ",
            "-f, --full-stacktrace                   Print out the full (very verbose)      ",
            "                                          stacktrace for any exceptions.       ",
            "-g, --gradle-user-home                  Specifies the gradle user home dir.    ",
            "-i, --ivy-quiet                         Set Ivy log level to quiet.            ",
            "-j, --ivy-debug                         Set Ivy log level to debug (very       ",
            "                                          verbose).                            ",
            "-l, --plugin-properties-file            Specifies the plugin.properties file.  ",
            "-n, --non-recursive                     Do not execute primary tasks of child  ",
            "                                          projects.                            ",
            "-p, --project-dir                       Specifies the start dir for Gradle.    ",
            "                                          Defaults to current dir.             ",
            "-q, --quiet                             Log errors only.                       ",
            "-r, --rebuild-cache                     Rebuild the cache of compiled build    ",
            "                                          scripts.                             ",
            "-s, --stacktrace                        Print out the stacktrace also for user ",
            "                                          exceptions (e.g. compile error).     ",
            "-t, --tasks                             Show list of all available tasks and   ",
            "                                          their dependencies.                  ",
            "-u, --no-search-upward                  Don't search in parent folders for a   ",
            "                                          settings.gradle file.                ",
            "-v, --version                           Print version info.                    ",
            "-x, --cache-off                         No caching of compiled build scripts.  " );
    }

    @Test
    public void dateConverterShouldShowDatePattern() throws Exception {
        parser.accepts( "date", "a date" ).withRequiredArg()
            .withValuesConvertedBy( datePattern( "MM/dd/yy" ) );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "--date <MM/dd/yy>                       a date                                 " );
    }

    @Test
    public void dateConverterShouldShowDatePatternInCombinationWithDescription()
        throws Exception {
        parser.accepts( "date", "a date" ).withOptionalArg()
            .describedAs( "your basic date pattern" )
            .withValuesConvertedBy( datePattern( "MM/dd/yy" ) );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "--date [MM/dd/yy: your basic date       a date                                 ",
            "  pattern]                                                                     " );
    }

    @Test
    public void shouldLeaveEmbeddedNewlinesInDescriptionsAlone() throws Exception {
        List<String> descriptionPieces =
            asList( "Specify the output type.", "'raw' = raw data.", "'java' = java class" );
        parser.accepts( "type", join( descriptionPieces, getProperty( "line.separator" ) ) );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "--type                                  Specify the output type.               ",
            "                                        'raw' = raw data.                      ",
            "                                        'java' = java class                    " );
    }

    @Test
    public void shouldIncludeDefaultValueForRequiredOptionArgument() throws Exception {
        parser.accepts( "a" ).withRequiredArg().defaultsTo( "boo" );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-a                                      (default: boo)                         " );
    }

    @Test
    public void shouldIncludeDefaultValueForOptionalOptionArgument() throws Exception {
        parser.accepts( "b" ).withOptionalArg().ofType( Integer.class ).defaultsTo( 5 );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-b [Integer]                            (default: 5)                           " );
    }

    @Test
    public void shouldIncludeDefaultValueForArgumentWithDescription() throws Exception {
        parser.accepts( "c", "a quantity" ).withOptionalArg().ofType( BigDecimal.class )
            .describedAs( "quantity" ).defaultsTo( TEN );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-c [BigDecimal: quantity]               a quantity (default: 10)               " );
    }

    @Test
    public void shouldIncludeListOfDefaultsForArgumentWithDescription() throws Exception {
        parser.accepts( "d", "dizzle" ).withOptionalArg().ofType( Integer.class )
            .describedAs( "double dizzle" ).defaultsTo( 2, 3, 5, 7 );

        parser.printHelpOn( sink );

        assertStandardHelpLines(
            "-d [Integer: double dizzle]             dizzle (default: [2, 3, 5, 7])         " );
    }

    @Test
    public void shouldMarkRequiredOptionsSpecially() throws Exception {
        parser.accepts( "e" ).withRequiredArg().required();

        parser.printHelpOn( sink );

        assertStandardHelpLinesWithRequiredIndicator(
            "* -e                                                                           " );
    }

    private void assertStandardHelpLines( String... expectedLines ) {
        List<String> lines = new ArrayList<String>();
        lines.add( EXPECTED_HEADER );
        lines.add( EXPECTED_SEPARATOR );
        addAll( lines, expectedLines );
        lines.add( EMPTY );

        assertHelpLines( lines.toArray( new String[ lines.size() ] ) );
    }

    private void assertStandardHelpLinesWithRequiredIndicator( String... expectedLines ) {
        List<String> lines = new ArrayList<String>();
        lines.add( EXPECTED_HEADER_WITH_REQUIRED_INDICATOR );
        lines.add( EXPECTED_SEPARATOR_WITH_REQUIRED_INDICATOR );
        addAll( lines, expectedLines );
        lines.add( EMPTY );

        assertHelpLines( lines.toArray( new String[ lines.size() ] ) );
    }

    private void assertHelpLines( String... expectedLines ) {
        assertEquals( join( expectedLines, LINE_SEPARATOR ), sink.toString() );
    }

    private static class FakeOutputStream extends ByteArrayOutputStream {
        boolean closed;
        boolean flushed;

        @Override
        public void close() {
            this.closed = true;
        }

        @Override
        public void flush() {
            this.flushed = true;
        }
    }
}
