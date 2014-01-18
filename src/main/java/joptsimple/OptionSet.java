/*
 The MIT License

 Copyright (c) 2004-2014 Paul R. Holser, Jr.

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

import java.util.*;

import static java.util.Collections.*;

import static joptsimple.internal.Objects.*;

/**
 * Representation of a group of detected command line options, their arguments, and non-option arguments.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class OptionSet {
    private final List<OptionSpec<?>> detectedSpecs;
    private final Map<String, OptionSpec<?>> detectedOptions;
    private final Map<OptionSpec<?>, List<String>> optionsToArguments;
    private final Map<String, ? extends OptionSpec<?>> recognizedSpecs;
    private final Map<String, List<?>> defaultValues;

    /*
     * Package-private because clients don't create these.
     */
    OptionSet( Map<String, ? extends OptionSpec<?>> recognizedSpecs ) {
        detectedSpecs = new ArrayList<OptionSpec<?>>();
        detectedOptions = new HashMap<String, OptionSpec<?>>();
        optionsToArguments = new IdentityHashMap<OptionSpec<?>, List<String>>();
        defaultValues = defaultValues( recognizedSpecs );
        this.recognizedSpecs = recognizedSpecs;
    }

    /**
     * Tells whether any options were detected.
     *
     * @return {@code true} if any options were detected
     */
    public boolean hasOptions() {
        return !detectedOptions.isEmpty();
    }

    /**
     * Tells whether the given option was detected.
     *
     * @param option the option to search for
     * @return {@code true} if the option was detected
     * @see #has(OptionSpec)
     */
    public boolean has( String option ) {
        return detectedOptions.containsKey( option );
    }

    /**
     * Tells whether the given option was detected.
     *
     * <p>This method recognizes only instances of options returned from the fluent interface methods.</p>
     *
     * <p>Specifying a {@linkplain ArgumentAcceptingOptionSpec#defaultsTo(Object, Object[])} default argument value}
     * for an option does not cause this method to return {@code true} if the option was not detected on the command
     * line.</p>
     *
     * @param option the option to search for
     * @return {@code true} if the option was detected
     * @see #has(String)
     */
    public boolean has( OptionSpec<?> option ) {
        return optionsToArguments.containsKey( option );
    }

    /**
     * Tells whether there are any arguments associated with the given option.
     *
     * @param option the option to search for
     * @return {@code true} if the option was detected and at least one argument was detected for the option
     * @see #hasArgument(OptionSpec)
     */
    public boolean hasArgument( String option ) {
        OptionSpec<?> spec = detectedOptions.get( option );
        return spec != null && hasArgument( spec );
    }

    /**
     * Tells whether there are any arguments associated with the given option.
     *
     * <p>This method recognizes only instances of options returned from the fluent interface methods.</p>
     *
     * <p>Specifying a {@linkplain ArgumentAcceptingOptionSpec#defaultsTo(Object, Object[]) default argument value}
     * for an option does not cause this method to return {@code true} if the option was not detected on the command
     * line, or if the option can take an optional argument but did not have one on the command line.</p>
     *
     * @param option the option to search for
     * @return {@code true} if the option was detected and at least one argument was detected for the option
     * @throws NullPointerException if {@code option} is {@code null}
     * @see #hasArgument(String)
     */
    public boolean hasArgument( OptionSpec<?> option ) {
        ensureNotNull( option );

        List<String> values = optionsToArguments.get( option );
        return values != null && !values.isEmpty();
    }

    /**
     * Gives the argument associated with the given option.  If the option was given an argument type, the argument
     * will take on that type; otherwise, it will be a {@link String}.
     *
     * <p>Specifying a {@linkplain ArgumentAcceptingOptionSpec#defaultsTo(Object, Object[]) default argument value}
     * for an option will cause this method to return that default value even if the option was not detected on the
     * command line, or if the option can take an optional argument but did not have one on the command line.</p>
     *
     * @param option the option to search for
     * @return the argument of the given option; {@code null} if no argument is present, or that option was not
     * detected
     * @throws NullPointerException if {@code option} is {@code null}
     * @throws OptionException if more than one argument was detected for the option
     */
    public Object valueOf( String option ) {
        ensureNotNull( option );

        OptionSpec<?> spec = detectedOptions.get( option );
        if ( spec == null ) {
            List<?> defaults = defaultValuesFor( option );
            return defaults.isEmpty() ? null : defaults.get( 0 );
        }

        return valueOf( spec );
    }

    /**
     * Gives the argument associated with the given option.
     *
     * <p>This method recognizes only instances of options returned from the fluent interface methods.</p>
     *
     * @param <V> represents the type of the arguments the given option accepts
     * @param option the option to search for
     * @return the argument of the given option; {@code null} if no argument is present, or that option was not
     * detected
     * @throws OptionException if more than one argument was detected for the option
     * @throws NullPointerException if {@code option} is {@code null}
     * @throws ClassCastException if the arguments of this option are not of the expected type
     */
    public <V> V valueOf( OptionSpec<V> option ) {
        ensureNotNull( option );

        List<V> values = valuesOf( option );
        switch ( values.size() ) {
            case 0:
                return null;
            case 1:
                return values.get( 0 );
            default:
                throw new MultipleArgumentsForOptionException( option.options() );
        }
    }

    /**
     * <p>Gives any arguments associated with the given option.  If the option was given an argument type, the
     * arguments will take on that type; otherwise, they will be {@link String}s.</p>
     *
     * @param option the option to search for
     * @return the arguments associated with the option, as a list of objects of the type given to the arguments; an
     * empty list if no such arguments are present, or if the option was not detected
     * @throws NullPointerException if {@code option} is {@code null}
     */
    public List<?> valuesOf( String option ) {
        ensureNotNull( option );

        OptionSpec<?> spec = detectedOptions.get( option );
        return spec == null ? defaultValuesFor( option ) : valuesOf( spec );
    }

    /**
     * <p>Gives any arguments associated with the given option.  If the option was given an argument type, the
     * arguments will take on that type; otherwise, they will be {@link String}s.</p>
     *
     * <p>This method recognizes only instances of options returned from the fluent interface methods.</p>
     *
     * @param <V> represents the type of the arguments the given option accepts
     * @param option the option to search for
     * @return the arguments associated with the option; an empty list if no such arguments are present, or if the
     * option was not detected
     * @throws NullPointerException if {@code option} is {@code null}
     * @throws OptionException if there is a problem converting the option's arguments to the desired type; for
     * example, if the type does not implement a correct conversion constructor or method
     */
    public <V> List<V> valuesOf( OptionSpec<V> option ) {
        ensureNotNull( option );

        List<String> values = optionsToArguments.get( option );
        if ( values == null || values.isEmpty() )
            return defaultValueFor( option );

        List<V> convertedValues = new ArrayList<V>();
        for ( String each : values )
            convertedValues.add( option.convert(each) );

        return unmodifiableList( convertedValues );
    }

    /**
     * Gives the set of options that were detected, in the form of {@linkplain OptionSpec}s, in the order in which the
     * options were found on the command line.
     *
     * @return the set of detected command line options
     */
    public List<OptionSpec<?>> specs() {
        List<OptionSpec<?>> specs = detectedSpecs;
        specs.remove( detectedOptions.get( NonOptionArgumentSpec.NAME ) );

        return unmodifiableList( specs );
    }

    /**
     * Gives all declared options as a map of string to {@linkplain OptionSpec}.
     *
     * @return the declared options as a map
     */
    public Map<OptionSpec<?>, List<?>> asMap() {
        Map<OptionSpec<?>, List<?>> map = new HashMap<OptionSpec<?>, List<?>>();
        for ( OptionSpec<?> spec : recognizedSpecs.values() )
            if ( !spec.representsNonOptions() )
                map.put( spec, valuesOf( spec ) );
        return unmodifiableMap( map );
    }

    /**
     * @return the detected non-option arguments
     */
    public List<?> nonOptionArguments() {
        return unmodifiableList( valuesOf( detectedOptions.get( NonOptionArgumentSpec.NAME ) ) );
    }

    void add( OptionSpec<?> spec ) {
        addWithArgument( spec, null );
    }

    void addWithArgument( OptionSpec<?> spec, String argument ) {
        detectedSpecs.add( spec );

        for ( String each : spec.options() )
            detectedOptions.put( each, spec );

        List<String> optionArguments = optionsToArguments.get( spec );

        if ( optionArguments == null ) {
            optionArguments = new ArrayList<String>();
            optionsToArguments.put( spec, optionArguments );
        }

        if ( argument != null )
            optionArguments.add( argument );
    }

    @Override
    public boolean equals( Object that ) {
        if ( this == that )
            return true;

        if ( that == null || !getClass().equals( that.getClass() ) )
            return false;

        OptionSet other = (OptionSet) that;
        Map<OptionSpec<?>, List<String>> thisOptionsToArguments =
            new HashMap<OptionSpec<?>, List<String>>( optionsToArguments );
        Map<OptionSpec<?>, List<String>> otherOptionsToArguments =
            new HashMap<OptionSpec<?>, List<String>>( other.optionsToArguments );
        return detectedOptions.equals( other.detectedOptions )
            && thisOptionsToArguments.equals( otherOptionsToArguments );
    }

    @Override
    public int hashCode() {
        Map<OptionSpec<?>, List<String>> thisOptionsToArguments =
            new HashMap<OptionSpec<?>, List<String>>( optionsToArguments );
        return detectedOptions.hashCode() ^ thisOptionsToArguments.hashCode();
    }

    @SuppressWarnings( "unchecked" )
    private <V> List<V> defaultValuesFor( String option ) {
        if ( defaultValues.containsKey( option ) )
            return (List<V>) defaultValues.get( option );

        return emptyList();
    }

    private <V> List<V> defaultValueFor( OptionSpec<V> option ) {
        return defaultValuesFor( option.options().iterator().next() );
    }

    private static Map<String, List<?>> defaultValues( Map<String, ? extends OptionSpec<?>> recognizedSpecs ) {
        Map<String, List<?>> defaults = new HashMap<String, List<?>>();
        for ( Map.Entry<String, ? extends OptionSpec<?>> each : recognizedSpecs.entrySet() )
            defaults.put( each.getKey(), each.getValue().defaultValues() );
        return defaults;
    }
}
