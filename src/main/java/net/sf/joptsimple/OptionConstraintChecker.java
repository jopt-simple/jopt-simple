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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Validates option constraints after parsing, such as required options and conditional availability.
 */
final class OptionConstraintChecker {
    private final Map<String, AbstractOptionSpec<?>> recognizedOptions;
    private final Map<List<String>, Set<OptionSpec<?>>> requiredIf;
    private final Map<List<String>, Set<OptionSpec<?>>> requiredUnless;
    private final Map<List<String>, Set<OptionSpec<?>>> availableIf;
    private final Map<List<String>, Set<OptionSpec<?>>> availableUnless;
    private final Function<String, AbstractOptionSpec<?>> specFor;

    OptionConstraintChecker(
        Map<String, AbstractOptionSpec<?>> recognizedOptions,
        Map<List<String>, Set<OptionSpec<?>>> requiredIf,
        Map<List<String>, Set<OptionSpec<?>>> requiredUnless,
        Map<List<String>, Set<OptionSpec<?>>> availableIf,
        Map<List<String>, Set<OptionSpec<?>>> availableUnless,
        Function<String, AbstractOptionSpec<?>> specFor ) {

        this.recognizedOptions = recognizedOptions;
        this.requiredIf = requiredIf;
        this.requiredUnless = requiredUnless;
        this.availableIf = availableIf;
        this.availableUnless = availableUnless;
        this.specFor = specFor;
    }

    void validate( OptionSet options ) {
        throwIfViolationsPresent( missingRequiredOptions( options ), MissingRequiredOptionsException::new, options );
        throwIfViolationsPresent( unavailableOptions( options ), UnavailableOptionException::new, options );
    }

    private void throwIfViolationsPresent(
        List<AbstractOptionSpec<?>> violations,
        Function<List<AbstractOptionSpec<?>>, OptionException> exceptionFactory,
        OptionSet options ) {

        if ( !violations.isEmpty() && !isHelpOptionPresent( options ) )
            throw exceptionFactory.apply( violations );
    }

    private List<AbstractOptionSpec<?>> missingRequiredOptions( OptionSet options ) {
        List<AbstractOptionSpec<?>> missingRequiredOptions = new ArrayList<>();

        for ( AbstractOptionSpec<?> each : recognizedOptions.values() ) {
            if ( each.isRequired() && !options.has( each ) )
                missingRequiredOptions.add( each );
        }

        for ( Map.Entry<List<String>, Set<OptionSpec<?>>> each : requiredIf.entrySet() ) {
            AbstractOptionSpec<?> required = specFor.apply( each.getKey().iterator().next() );

            if ( optionsHasAnyOf( options, each.getValue() ) && !options.has( required ) )
                missingRequiredOptions.add( required );
        }

        for ( Map.Entry<List<String>, Set<OptionSpec<?>>> each : requiredUnless.entrySet() ) {
            AbstractOptionSpec<?> required = specFor.apply( each.getKey().iterator().next() );

            if ( !optionsHasAnyOf( options, each.getValue() ) && !options.has( required ) )
                missingRequiredOptions.add( required );
        }

        return missingRequiredOptions;
    }

    private List<AbstractOptionSpec<?>> unavailableOptions( OptionSet options ) {
        List<AbstractOptionSpec<?>> unavailableOptions = new ArrayList<>();

        for ( Map.Entry<List<String>, Set<OptionSpec<?>>> eachEntry : availableIf.entrySet() ) {
            AbstractOptionSpec<?> forbidden = specFor.apply( eachEntry.getKey().iterator().next() );

            if ( !optionsHasAnyOf( options, eachEntry.getValue() ) && options.has( forbidden ) )
                unavailableOptions.add( forbidden );
        }

        for ( Map.Entry<List<String>, Set<OptionSpec<?>>> eachEntry : availableUnless.entrySet() ) {
            AbstractOptionSpec<?> forbidden = specFor.apply( eachEntry.getKey().iterator().next() );

            if ( optionsHasAnyOf( options, eachEntry.getValue() ) && options.has( forbidden ) )
                unavailableOptions.add( forbidden );
        }

        return unavailableOptions;
    }

    private static boolean optionsHasAnyOf( OptionSet options, Collection<OptionSpec<?>> specs ) {
        for ( OptionSpec<?> each : specs ) {
            if ( options.has( each ) )
                return true;
        }

        return false;
    }

    private boolean isHelpOptionPresent( OptionSet options ) {
        for ( AbstractOptionSpec<?> each : recognizedOptions.values() ) {
            if ( each.isForHelp() && options.has( each ) )
                return true;
        }

        return false;
    }
}
