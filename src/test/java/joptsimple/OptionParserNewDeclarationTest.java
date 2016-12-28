/*
 The MIT License

 Copyright (c) 2004-2016 Paul R. Holser, Jr.

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

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Optional;

import static java.lang.Boolean.*;
import static java.util.Collections.*;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class OptionParserNewDeclarationTest extends AbstractOptionParserFixture {
    @Test( expected = IllegalOptionSpecificationException.class )
    public void acceptsIllegalCharacters() {
        parser.accepts( "!" );
    }

    @Test
    public void booleanArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Boolean.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Boolean.class );

        OptionSet options = parser.parse( "-a", "true", "-b", "false" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( TRUE, options.valueOf( "a" ) );
        assertEquals( Optional.of( true ) , options.valueOfOptional( "a" ) );
        assertEquals( singletonList( true ), options.valuesOf( "a" ) );
        assertEquals( FALSE, options.valueOf( "b" ) );
        assertEquals( Optional.of( false ) , options.valueOfOptional( "b" ) );
        assertEquals( singletonList( false ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void byteArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Byte.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Byte.class );

        OptionSet options = parser.parse( "-a", "-1", "-b", "-2" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Byte.valueOf( "-1" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( Byte.valueOf( "-1" ) ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( Byte.valueOf( "-1" ) ), options.valuesOf( "a" ) );
        assertEquals( Byte.valueOf( "-2" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( Byte.valueOf( "-2" ) ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( Byte.valueOf( "-2" ) ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void doubleArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Double.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Double.class );

        OptionSet options = parser.parse( "-a", "3.1415926D", "-b", "6.02E23" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Double.valueOf( "3.1415926D" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( 3.1415926D ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( 3.1415926D ), options.valuesOf( "a" ) );
        assertEquals( Double.valueOf( "6.02E23" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( 6.02E23 ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( 6.02E23 ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void floatArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Float.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Float.class );

        OptionSet options = parser.parse( "-a", "3.1415926F", "-b", "6.02E23F" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Float.valueOf( "3.1415926F" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( 3.1415926F ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( 3.1415926F ), options.valuesOf( "a" ) );
        assertEquals( Float.valueOf( "6.02E23F" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( 6.02E23F ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( 6.02E23F ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void integerArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Integer.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Integer.class );

        OptionSet options = parser.parse( "-a", "12", "-b", "34" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Integer.valueOf( "12" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( 12 ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( 12 ), options.valuesOf( "a" ) );
        assertEquals( Integer.valueOf( "34" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( 34 ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( 34 ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void longArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Long.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Long.class );

        OptionSet options = parser.parse( "-a", "123454678901234", "-b", "98765432109876" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Long.valueOf( "123454678901234" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( 123454678901234L ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( 123454678901234L ), options.valuesOf( "a" ) );
        assertEquals( Long.valueOf( "98765432109876" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( 98765432109876L ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( 98765432109876L ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void shortArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Short.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Short.class );

        OptionSet options = parser.parse( "-a", "5675", "-b", "345" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Short.valueOf( "5675" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( Short.valueOf( "5675" ) ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( Short.valueOf( "5675" ) ), options.valuesOf( "a" ) );
        assertEquals( Short.valueOf( "345" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( Short.valueOf( "345" ) ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( Short.valueOf( "345" ) ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void sqlDateArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( java.sql.Date.class );
        parser.accepts( "b" ).withOptionalArg().ofType( java.sql.Date.class );

        OptionSet options = parser.parse( "-a", "2001-09-11", "-b", "1941-12-07" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( java.sql.Date.valueOf( "2001-09-11" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( java.sql.Date.valueOf( "2001-09-11" ) ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( java.sql.Date.valueOf( "2001-09-11" ) ), options.valuesOf( "a" ) );
        assertEquals( java.sql.Date.valueOf( "1941-12-07" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( java.sql.Date.valueOf( "1941-12-07" ) ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( java.sql.Date.valueOf( "1941-12-07" ) ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void sqlTimeArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Time.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Time.class );

        OptionSet options = parser.parse( "-a", "08:57:39", "-b", "23:59:59" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Time.valueOf( "08:57:39" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( Time.valueOf( "08:57:39" ) ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( Time.valueOf( "08:57:39" ) ), options.valuesOf( "a" ) );
        assertEquals( Time.valueOf( "23:59:59" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( Time.valueOf( "23:59:59" ) ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( Time.valueOf( "23:59:59" ) ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void sqlTimestampArgumentType() {
        parser.accepts( "a" ).withRequiredArg().ofType( Timestamp.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Timestamp.class );

        OptionSet options = parser.parse( "-a", "1970-01-01 00:00:00", "-b", "1979-12-31 23:59:59.0123456" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Timestamp.valueOf( "1970-01-01 00:00:00" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( Timestamp.valueOf( "1970-01-01 00:00:00" ) ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( Timestamp.valueOf( "1970-01-01 00:00:00" ) ), options.valuesOf( "a" ) );
        assertEquals( Timestamp.valueOf( "1979-12-31 23:59:59.0123456" ), options.valueOf( "b" ) );
        assertEquals(
            Optional.of( Timestamp.valueOf( "1979-12-31 23:59:59.0123456" ) ),
            options.valueOfOptional( "b" ) );
        assertEquals( singletonList( Timestamp.valueOf( "1979-12-31 23:59:59.0123456" ) ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test( expected = OptionException.class )
    public void illegalOptionArgumentMethodConversion() {
        parser.accepts( "a" ).withRequiredArg().ofType( Integer.class );

        OptionSet options = parser.parse( "-a", "foo" );

        options.valueOf( "a" );
    }

    @Test( expected = OptionException.class )
    public void illegalOptionArgumentConstructorConversion() {
        parser.accepts( "a" ).withRequiredArg().ofType( BigInteger.class );

        OptionSet options = parser.parse( "-a", "foo" );

        options.valueOf( "a" );
    }

    @Test
    public void optionsWithOptionalNegativeNumberArguments() {
        parser.accepts( "a" ).withOptionalArg().ofType( Integer.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Integer.class );
        parser.accepts( "1" );
        parser.accepts( "2" );

        OptionSet options = parser.parse( "-a", "-1", "-b", "-2" );

        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertEquals( Integer.valueOf( "-1" ), options.valueOf( "a" ) );
        assertEquals( Optional.of( -1 ), options.valueOfOptional( "a" ) );
        assertEquals( singletonList( -1 ), options.valuesOf( "a" ) );
        assertEquals( Integer.valueOf( "-2" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( -2 ), options.valueOfOptional( "b" ) );
        assertEquals( singletonList( -2 ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void optionsWithOptionalNegativeNumberArgumentsAndNumberOptions() {
        parser.accepts( "a" ).withOptionalArg().ofType( Integer.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Integer.class );
        parser.accepts( "1" );
        parser.accepts( "2" );

        OptionSet options = parser.parse( "-1", "-2", "-a", "-b" );

        assertOptionDetected( options, "1" );
        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "2" );
        assertOptionDetected( options, "b" );
        assertNull( options.valueOf( "1" ) );
        assertNull( options.valueOf( "a" ) );
        assertNull( options.valueOf( "2" ) );
        assertNull( options.valueOf( "b" ) );
        assertEquals( Optional.empty(), options.valueOfOptional( "1" ) );
        assertEquals( Optional.empty(), options.valueOfOptional( "a" ) );
        assertEquals( Optional.empty(), options.valueOfOptional( "2" ) );
        assertEquals( Optional.empty(), options.valueOfOptional( "b" ) );
        assertEquals( emptyList(), options.valuesOf( "1" ) );
        assertEquals( emptyList(), options.valuesOf( "a" ) );
        assertEquals( emptyList(), options.valuesOf( "2" ) );
        assertEquals( emptyList(), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }

    @Test
    public void optionsWithNegativeNumberArgumentsAndNonNumberOptions() {
        parser.accepts( "a" ).withOptionalArg().ofType( Integer.class );
        parser.accepts( "b" ).withOptionalArg().ofType( Integer.class );
        parser.accepts( "1" );
        parser.accepts( "2" );

        OptionSet options = parser.parse( "-1", "-a", "-b", "-2" );

        assertOptionDetected( options, "1" );
        assertOptionDetected( options, "a" );
        assertOptionDetected( options, "b" );
        assertOptionNotDetected( options, "2" );
        assertNull( options.valueOf( "1" ) );
        assertEquals( Optional.empty(), options.valueOfOptional( "1" ) );
        assertNull( options.valueOf( "a" ) );
        assertEquals( Optional.empty(), options.valueOfOptional( "a" ) );
        assertEquals( Integer.valueOf( "-2" ), options.valueOf( "b" ) );
        assertEquals( Optional.of( -2 ), options.valueOfOptional( "b" ) );
        assertEquals( emptyList(), options.valuesOf( "1" ) );
        assertEquals( emptyList(), options.valuesOf( "a" ) );
        assertEquals( singletonList( -2 ), options.valuesOf( "b" ) );
        assertEquals( emptyList(), options.nonOptionArguments() );
    }
}
