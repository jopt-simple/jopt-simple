/*
 The MIT License

 Copyright (c) 2004-2021 Paul R. Holser, Jr.

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

package net.sf.joptsimple;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class OptionConstraintsWithArgumentsTest extends AbstractOptionParserFixture {
    @ParameterizedTest
    @ValueSource( booleans = { false, true } )
    public void rejectsMutuallyExclusiveOptionsAfterTheyAcceptArguments( boolean required ) {
        OptionSpecBuilder enable = parser.acceptsAll( asList( "e", "enable" ) );
        OptionSpecBuilder disable = parser.acceptsAll( asList( "d", "disable" ) );
        parser.mutuallyExclusive( enable, disable );
        acceptsArguments( enable, required );
        acceptsArguments( disable, required );

        assertThrows( UnavailableOptionException.class, () -> parser.parse( "-e=yes", "--disable=no" ) );
    }

    @ParameterizedTest
    @ValueSource( booleans = { false, true } )
    public void acceptsSingleMutuallyExclusiveOptionAfterItAcceptsArguments( boolean required ) {
        OptionSpecBuilder enable = parser.acceptsAll( asList( "e", "enable" ) );
        OptionSpecBuilder disable = parser.acceptsAll( asList( "d", "disable" ) );
        parser.mutuallyExclusive( enable, disable );
        ArgumentAcceptingOptionSpec<String> enableWithArgument = acceptsArguments( enable, required );
        acceptsArguments( disable, required );

        OptionSet options = parser.parse( "-e=yes" );

        assertOptionDetected( options, "enable" );
        assertOptionNotDetected( options, "disable" );
        assertEquals( "yes", options.valueOf( enableWithArgument ) );
    }

    @ParameterizedTest
    @ValueSource( booleans = { false, true } )
    public void requiresOptionWhenItsArgumentAcceptingDependencyIsPresent( boolean required ) {
        OptionSpecBuilder trigger = parser.acceptsAll( asList( "t", "trigger" ) );
        parser.accepts( "target" ).requiredIf( trigger );
        acceptsArguments( trigger, required );

        assertThrows( MissingRequiredOptionsException.class, () -> parser.parse( "-t=yes" ) );

        OptionSet options = parser.parse( "--trigger=yes", "--target" );
        assertOptionDetected( options, "target" );
    }

    @ParameterizedTest
    @ValueSource( booleans = { false, true } )
    public void doesNotRequireOptionWhenItsArgumentAcceptingDependencyIsPresent( boolean required ) {
        OptionSpecBuilder trigger = parser.acceptsAll( asList( "t", "trigger" ) );
        parser.accepts( "target" ).requiredUnless( "trigger" );
        acceptsArguments( trigger, required );

        OptionSet options = parser.parse( "-t=yes" );

        assertOptionDetected( options, "trigger" );
        assertOptionNotDetected( options, "target" );
        assertThrows( MissingRequiredOptionsException.class, () -> parser.parse() );
    }

    @ParameterizedTest
    @ValueSource( booleans = { false, true } )
    public void allowsOptionWhenItsArgumentAcceptingDependencyIsPresent( boolean required ) {
        OptionSpecBuilder trigger = parser.acceptsAll( asList( "t", "trigger" ) );
        parser.accepts( "target" ).availableIf( "trigger" );
        acceptsArguments( trigger, required );

        OptionSet options = parser.parse( "-t=yes", "--target" );

        assertOptionDetected( options, "target" );
        assertThrows( UnavailableOptionException.class, () -> parser.parse( "--target" ) );
    }

    @ParameterizedTest
    @ValueSource( booleans = { false, true } )
    public void forbidsOptionWhenItsArgumentAcceptingDependencyIsPresent( boolean required ) {
        OptionSpecBuilder trigger = parser.acceptsAll( asList( "t", "trigger" ) );
        parser.accepts( "target" ).availableUnless( trigger );
        acceptsArguments( trigger, required );

        assertThrows( UnavailableOptionException.class, () -> parser.parse( "-t=yes", "--target" ) );

        OptionSet options = parser.parse( "--target" );
        assertOptionDetected( options, "target" );
    }

    @Test
    public void doesNotConfuseRedefinedOptionWithItsFormerAlias() {
        parser.acceptsAll( asList( "a", "alpha" ) );
        OptionSpecBuilder redefined = parser.accepts( "a" );
        parser.accepts( "target" ).requiredIf( redefined );

        OptionSet options = parser.parse( "--alpha" );

        assertOptionNotDetected( options, "target" );
    }

    @Test
    public void recognizesRemainingAliasWhenAnotherAliasIsRedefined() {
        OptionSpecBuilder trigger = parser.acceptsAll( asList( "a", "alpha" ) );
        parser.accepts( "target" ).requiredIf( trigger );
        parser.accepts( "a" );

        assertThrows( MissingRequiredOptionsException.class, () -> parser.parse( "--alpha" ) );
    }

    private static ArgumentAcceptingOptionSpec<String> acceptsArguments( OptionSpecBuilder option, boolean required ) {
        return required ? option.withRequiredArg() : option.withOptionalArg();
    }
}
