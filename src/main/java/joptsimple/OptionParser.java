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

import joptsimple.internal.AbbreviationMap;
import joptsimple.util.KeyValuePair;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonList;
import static joptsimple.OptionException.unrecognizedOption;
import static joptsimple.OptionOrder.TRAINING_ORDER;
import static joptsimple.OptionParserState.moreOptions;
import static joptsimple.ParserRules.RESERVED_FOR_EXTENSIONS;
import static joptsimple.ParserRules.ensureLegalOptions;
import static joptsimple.ParserRules.isLongOptionToken;
import static joptsimple.ParserRules.isShortOptionToken;

/**
 * <p>Parses command line arguments, using a syntax that attempts to take from the best of POSIX {@code getopt()}
 * and GNU {@code getopt_long()}.</p>
 *
 * <p>This parser supports short options and long options.</p>
 *
 * <ul>
 *   <li><dfn>Short options</dfn> begin with a single hyphen ("<kbd>-</kbd>") followed by a single letter or digit,
 *   or question mark ("<kbd>?</kbd>"), or dot ("<kbd>.</kbd>").</li>
 *
 *   <li>Short options can accept single arguments. The argument can be made required or optional. The option's
 *   argument can occur:
 *     <ul>
 *       <li>in the slot after the option, as in <kbd>-d /tmp</kbd></li>
 *       <li>right up against the option, as in <kbd>-d/tmp</kbd></li>
 *       <li>right up against the option separated by an equals sign (<kbd>"="</kbd>), as in <kbd>-d=/tmp</kbd></li>
 *     </ul>
 *   To specify <em>n</em> arguments for an option, specify the option <em>n</em> times, once for each argument,
 *   as in <kbd>-d /tmp -d /var -d /opt</kbd>; or, when using the
 *   {@linkplain ArgumentAcceptingOptionSpec#withValuesSeparatedBy(char) "separated values"} clause of the "fluent
 *   interface" (see below), give multiple values separated by a given character as a single argument to the
 *   option.</li>
 *
 *   <li>Short options can be clustered, so that <kbd>-abc</kbd> is treated as <kbd>-a -b -c</kbd>. If a short option
 *   in the cluster can accept an argument, the remaining characters are interpreted as the argument for that
 *   option.</li>
 *
 *   <li>An argument consisting only of two hyphens (<kbd>"--"</kbd>) signals that the remaining arguments are to be
 *   treated as non-options.</li>
 *
 *   <li>An argument consisting only of a single hyphen is considered a non-option argument (though it can be an
 *   argument of an option). Many Unix programs treat single hyphens as stand-ins for the standard input or standard
 *   output streams.</li>
 *
 *   <li><dfn>Long options</dfn> begin with two hyphens (<kbd>"--"</kbd>), followed by multiple letters, digits,
 *   hyphens, question marks, or dots. A hyphen cannot be the first character of a long option specification when
 *   configuring the parser.</li>
 *
 *   <li>You can abbreviate long options, so long as the abbreviation is unique.</li>
 *
 *   <li>Long options can accept single arguments.  The argument can be made required or optional.  The option's
 *   argument can occur:
 *     <ul>
 *       <li>in the slot after the option, as in <kbd>--directory /tmp</kbd></li>
 *       <li>right up against the option separated by an equals sign (<kbd>"="</kbd>), as in
 *       <kbd>--directory=/tmp</kbd>
 *     </ul>
 *   Specify multiple arguments for a long option in the same manner as for short options (see above).</li>
 *
 *   <li>You can use a single hyphen (<kbd>"-"</kbd>) instead of a double hyphen (<kbd>"--"</kbd>) for a long
 *   option.</li>
 *
 *   <li>The option <kbd>-W</kbd> is reserved.  If you tell the parser to {@linkplain
 *   #recognizeAlternativeLongOptions(boolean) recognize alternative long options}, then it will treat, for example,
 *   <kbd>-W foo=bar</kbd> as the long option <kbd>foo</kbd> with argument <kbd>bar</kbd>, as though you had written
 *   <kbd>--foo=bar</kbd>.</li>
 *
 *   <li>You can specify <kbd>-W</kbd> as a valid short option, or use it as an abbreviation for a long option, but
 *   {@linkplain #recognizeAlternativeLongOptions(boolean) recognizing alternative long options} will always supersede
 *   this behavior.</li>
 *
 *   <li>You can specify a given short or long option multiple times on a single command line. The parser collects
 *   any arguments specified for those options as a list.</li>
 *
 *   <li>If the parser detects an option whose argument is optional, and the next argument "looks like" an option,
 *   that argument is not treated as the argument to the option, but as a potentially valid option. If, on the other
 *   hand, the optional argument is typed as a derivative of {@link Number}, then that argument is treated as the
 *   negative number argument of the option, even if the parser recognizes the corresponding numeric option.
 *   For example:
 *   <pre><code>
 *     OptionParser parser = new OptionParser();
 *     parser.accepts( "a" ).withOptionalArg().ofType( Integer.class );
 *     parser.accepts( "2" );
 *     OptionSet options = parser.parse( "-a", "-2" );
 *   </code></pre>
 *   In this case, the option set contains <kbd>"a"</kbd> with argument <kbd>-2</kbd>, not both <kbd>"a"</kbd> and
 *   <kbd>"2"</kbd>. Swapping the elements in the <em>args</em> array gives the latter.</li>
 * </ul>
 *
 * <p>There are two ways to tell the parser what options to recognize:</p>
 *
 * <ol>
 *   <li>A "fluent interface"-style API for specifying options, available since version 2. Sentences in this fluent
 *   interface language begin with a call to {@link #accepts(String) accepts} or {@link #acceptsAll(Collection)
 *   acceptsAll} methods; calls on the ensuing chain of objects describe whether the options can take an argument,
 *   whether the argument is required or optional, to what type arguments of the options should be converted if any,
 *   etc. Since version 3, these calls return an instance of {@link OptionSpec}, which can subsequently be used to
 *   retrieve the arguments of the associated option in a type-safe manner.</li>
 *
 *   <li>Since version 1, a more concise way of specifying short options has been to use the special {@linkplain
 *   #OptionParser(String) constructor}. Arguments of options specified in this manner will be of type {@link String}.
 *   Here are the rules for the format of the specification strings this constructor accepts:
 *
 *     <ul>
 *       <li>Any letter or digit is treated as an option character.</li>
 *
 *       <li>An option character can be immediately followed by an asterisk (*) to indicate that the option is a
 *       "help" option.</li>
 *
 *       <li>If an option character (with possible trailing asterisk) is followed by a single colon (<kbd>":"</kbd>),
 *       then the option requires an argument.</li>
 *
 *       <li>If an option character (with possible trailing asterisk) is followed by two colons (<kbd>"::"</kbd>),
 *       then the option accepts an optional argument.</li>
 *
 *       <li>Otherwise, the option character accepts no argument.</li>
 *
 *       <li>If the option specification string begins with a plus sign (<kbd>"+"</kbd>), the parser will behave
 *       "POSIX-ly correct".</li>
 *
 *       <li>If the option specification string contains the sequence <kbd>"W;"</kbd> (capital W followed by a
 *       semicolon), the parser will recognize the alternative form of long options.</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p>Each of the options in a list of options given to {@link #acceptsAll(Collection) acceptsAll} is treated as a
 * synonym of the others.  For example:
 *   <pre>
 *     <code>
 *     OptionParser parser = new OptionParser();
 *     parser.acceptsAll( asList( "w", "interactive", "confirmation" ) );
 *     OptionSet options = parser.parse( "-w" );
 *     </code>
 *   </pre>
 * In this case, <code>options.{@link OptionSet#has(String) has}</code> would answer {@code true} when given arguments
 * <kbd>"w"</kbd>, <kbd>"interactive"</kbd>, and <kbd>"confirmation"</kbd>. The {@link OptionSet} would give the same
 * responses to these arguments for its other methods as well.</p>
 *
 * <p>By default, as with GNU {@code getopt()}, the parser allows intermixing of options and non-options. If, however,
 * the parser has been created to be "POSIX-ly correct", then the first argument that does not look lexically like an
 * option, and is not a required argument of a preceding option, signals the end of options. You can still bind
 * optional arguments to their options using the abutting (for short options) or <kbd>=</kbd> syntax.</p>
 *
 * <p>Unlike GNU {@code getopt()}, this parser does not honor the environment variable {@code POSIXLY_CORRECT}.
 * "POSIX-ly correct" parsers are configured by either:</p>
 *
 * <ol>
 *   <li>using the method {@link #posixlyCorrect(boolean)}, or</li>
 *
 *   <li>using the {@linkplain #OptionParser(String) constructor} with an argument whose first character is a plus sign
 *   (<kbd>"+"</kbd>)</li>
 * </ol>
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 * @see <a href="http://www.gnu.org/software/libc/manual">The GNU C Library</a>
 */
public class OptionParser implements OptionDeclarer {
    private final AbbreviationMap<AbstractOptionSpec<?>> recognizedOptions;
    private final List<OptionSpec<?>> trainingOrder;
    private final Map<Collection<String>, Set<OptionSpec<?>>> requiredIf;
    private final Map<Collection<String>, Set<OptionSpec<?>>> requiredUnless;
    private OptionParserState state;
    private boolean posixlyCorrect;
    private boolean allowsUnrecognizedOptions;
    private HelpFormatter helpFormatter = new BuiltinHelpFormatter();

    /**
     * Creates an option parser that initially recognizes no options, and does not exhibit "POSIX-ly correct"
     * behavior.
     */
    public OptionParser() {
        recognizedOptions = new AbbreviationMap<AbstractOptionSpec<?>>();
        trainingOrder = new ArrayList<OptionSpec<?>>();
        requiredIf = new HashMap<Collection<String>, Set<OptionSpec<?>>>();
        requiredUnless = new HashMap<Collection<String>, Set<OptionSpec<?>>>();
        state = moreOptions( false );

        recognize( new NonOptionArgumentSpec<String>() );
    }

    /**
     * Creates an option parser and configures it to recognize the short options specified in the given string.
     *
     * Arguments of options specified this way will be of type {@link String}.
     *
     * @param optionSpecification an option specification
     * @throws NullPointerException if {@code optionSpecification} is {@code null}
     * @throws OptionException if the option specification contains illegal characters or otherwise cannot be
     * recognized
     */
    public OptionParser( String optionSpecification ) {
        this();

        new OptionSpecTokenizer( optionSpecification ).configure( this );
    }

    public OptionSpecBuilder accepts( String option ) {
        return acceptsAll( singletonList( option ) );
    }

    public OptionSpecBuilder accepts( String option, String description ) {
        return acceptsAll( singletonList( option ), description );
    }

    public OptionSpecBuilder acceptsAll( Collection<String> options ) {
        return acceptsAll( options, "" );
    }

    public OptionSpecBuilder acceptsAll( Collection<String> options, String description ) {
        if ( options.isEmpty() )
            throw new IllegalArgumentException( "need at least one option" );

        ensureLegalOptions( options );

        return new OptionSpecBuilder( this, options, description );
    }

    public NonOptionArgumentSpec<String> nonOptions() {
        NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<String>();

        recognize( spec );

        return spec;
    }

    public NonOptionArgumentSpec<String> nonOptions( String description ) {
        NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<String>( description );

        recognize( spec );

        return spec;
    }

    public void posixlyCorrect( boolean setting ) {
        posixlyCorrect = setting;
        state = moreOptions( setting );
    }

    boolean posixlyCorrect() {
        return posixlyCorrect;
    }

    public void allowsUnrecognizedOptions() {
        allowsUnrecognizedOptions = true;
    }

    boolean doesAllowsUnrecognizedOptions() {
        return allowsUnrecognizedOptions;
    }

    /** @todo Test/fix for trainingOrder */
    public void recognizeAlternativeLongOptions( boolean recognize ) {
        if ( recognize )
            recognize( new AlternativeLongOptionSpec() );
        else
            recognizedOptions.remove( String.valueOf( RESERVED_FOR_EXTENSIONS ) );
    }

    void recognize( AbstractOptionSpec<?> spec ) {
        recognizedOptions.putAll(spec.options(), spec);
        trainingOrder.add( spec );
    }

    /**
     * Provides a fluent alternative to {@link #printHelpOn(OutputStream)} and friends.
     *
     * See {@link #printHelpOn(OutputStream)} for one-call alternative
     */
    public static class HelpPrinter {
        private final OptionParser optionParser;
        private HelpFormatter helpFormatter;

        private HelpPrinter( final OptionParser optionParser ) {
            this.optionParser = optionParser;
        }

        /**
         * Changes the help formatter. The default otherwise is to use the help formatter of the option parser.
         *
         * @param helpFormatter the help formatter, never missing
         * @return the help printer, never missing
         */
        public HelpPrinter formatWith( HelpFormatter helpFormatter ) {
            this.helpFormatter = helpFormatter;
            return this;
        }

        /**
         * Writes information about the options this parser recognizes to the given output sink.
         *
         * The output sink is flushed, but not closed.
         *
         * @param sink the sink to write information to
         * @throws IOException if there is a problem writing to the sink
         * @throws NullPointerException if {@code sink} is {@code null}
         * @see #on(Writer)
         */
        public void on( OutputStream sink ) throws IOException {
            on( new OutputStreamWriter( sink ) );
        }

        /**
         * Writes information about the options this parser recognizes to the given output sink.
         *
         * The output sink is flushed, but not closed.
         *
         * @param sink the sink to write information to
         * @throws IOException if there is a problem writing to the sink
         * @throws NullPointerException if {@code sink} is {@code null}
         * @see #on(OutputStream)
         */
        public void on( Writer sink ) throws IOException {
            sink.write( formatHelp() );
            sink.flush();
        }

        private String formatHelp() {
            return helpFormatter().format( optionParser );
        }

        private HelpFormatter helpFormatter() {
            return null == helpFormatter ? optionParser.helpFormatter : helpFormatter;
        }
    }

    /**
     * Provides a fluent alternative to {@link #printHelpOn(OutputStream)} and friends.
     *
     * @see #printHelpOn(OutputStream)
     * @since 4.7
     */
    public HelpPrinter printHelp() {
        return new HelpPrinter( this );
    }

    /**
     * Writes information about the options this parser recognizes to the given output sink.
     *
     * The output sink is flushed, but not closed.
     *
     * @param sink the sink to write information to
     * @throws IOException if there is a problem writing to the sink
     * @throws NullPointerException if {@code sink} is {@code null}
     * @see #printHelpOn(Writer)
     * @see #printHelp()
     */
    public void printHelpOn( OutputStream sink ) throws IOException {
        printHelpOn( new OutputStreamWriter( sink ) );
    }

    /**
     * Writes information about the options this parser recognizes to the given output sink.
     *
     * The output sink is flushed, but not closed.
     *
     * @param sink the sink to write information to
     * @throws IOException if there is a problem writing to the sink
     * @throws NullPointerException if {@code sink} is {@code null}
     * @see #printHelpOn(OutputStream)
     * @see #printHelp()
     */
    public void printHelpOn( Writer sink ) throws IOException {
        sink.write( helpFormatter.format( this ) );
        sink.flush();
    }

    /**
     * Tells the parser to use the given formatter when asked to {@linkplain #printHelpOn(java.io.Writer) print help}.
     *
     * @param formatter the formatter to use for printing help
     * @throws NullPointerException if the formatter is {@code null}
     */
    public void formatHelpWith( HelpFormatter formatter ) {
        if ( formatter == null )
            throw new NullPointerException();

        helpFormatter = formatter;
    }

    /**
     * Retrieves all options-spec pairings which have been configured for the parser in the same order as declared
     * during training. Option flags for specs are alphabetized by {@link OptionSpec#options()}; only the order of the
     * specs is preserved.
     *
     * Note: the return type has changed since 4.6.
     *
     * @return a map containing all the configured options and their corresponding {@link OptionSpec}
     * @since 4.7
     */
    public Map<String, ? extends OptionSpec<?>> recognizedOptions() {
        return OptionOrder.asMap( TRAINING_ORDER.of( this ) );
    }

    List<? extends OptionSpec<?>> abbreviationOrder() {
        return new ArrayList<OptionSpec<?>>( recognizedOptions.values() );
    }

    List<? extends OptionSpec<?>> trainingOrder() {
        return new ArrayList<OptionSpec<?>>( trainingOrder );
    }

    /**
     * Parses the given command line arguments according to the option specifications given to the parser.
     *
     * @param arguments arguments to parse
     * @return an {@link OptionSet} describing the parsed options, their arguments, and any non-option arguments found
     * @throws OptionException if problems are detected while parsing
     * @throws NullPointerException if the argument list is {@code null}
     */
    public OptionSet parse( String... arguments ) {
        ArgumentList argumentList = new ArgumentList( arguments );
        OptionSet detected = new OptionSet( recognizedOptions.toJavaUtilMap() );
        detected.add( recognizedOptions.get( NonOptionArgumentSpec.NAME ) );

        while ( argumentList.hasMore() )
            state.handleArgument( this, argumentList, detected );

        reset();

        ensureRequiredOptions( detected );

        return detected;
    }

    private void ensureRequiredOptions( OptionSet options ) {
        Collection<String> missingRequiredOptions = missingRequiredOptions( options );
        boolean helpOptionPresent = isHelpOptionPresent( options );

        if ( !missingRequiredOptions.isEmpty() && !helpOptionPresent )
            throw new MissingRequiredOptionException( missingRequiredOptions );
    }

    private Collection<String> missingRequiredOptions( OptionSet options ) {
        Collection<String> missingRequiredOptions = new HashSet<String>();

        for ( OptionSpec<?> each : abbreviationOrder() ) {
            if ( each.isRequired() && !options.has( each ) )
                missingRequiredOptions.addAll( each.options() );
        }

        for ( Map.Entry<Collection<String>, Set<OptionSpec<?>>> eachEntry : requiredIf.entrySet() ) {
            OptionSpec<?> required = specFor( eachEntry.getKey().iterator().next() );

            if ( optionsHasAnyOf( options, eachEntry.getValue() ) && !options.has( required ) ) {
                missingRequiredOptions.addAll( required.options() );
            }
        }

        for ( Map.Entry<Collection<String>, Set<OptionSpec<?>>> eachEntry : requiredUnless.entrySet() ) {
            OptionSpec<?> required = specFor( eachEntry.getKey().iterator().next() );

            if ( !optionsHasAnyOf( options, eachEntry.getValue() ) && !options.has( required ) ) {
                missingRequiredOptions.addAll( required.options() );
            }
        }

        return missingRequiredOptions;
    }

    private boolean optionsHasAnyOf( OptionSet options, Collection<OptionSpec<?>> specs ) {
        for ( OptionSpec<?> each : specs ) {
            if ( options.has( each ) )
                return true;
        }

        return false;
    }

    private boolean isHelpOptionPresent( OptionSet options ) {
        boolean helpOptionPresent = false;
        for ( OptionSpec<?> each : abbreviationOrder() ) {
            if ( each.isForHelp() && options.has( each ) ) {
                helpOptionPresent = true;
                break;
            }
        }
        return helpOptionPresent;
    }

    void handleLongOptionToken( String candidate, ArgumentList arguments, OptionSet detected ) {
        KeyValuePair optionAndArgument = parseLongOptionWithArgument( candidate );

        if ( !isRecognized( optionAndArgument.key ) )
            throw unrecognizedOption( optionAndArgument.key );

        AbstractOptionSpec<?> optionSpec = specFor( optionAndArgument.key );
        optionSpec.handleOption( this, arguments, detected, optionAndArgument.value );
    }

    void handleShortOptionToken( String candidate, ArgumentList arguments, OptionSet detected ) {
        KeyValuePair optionAndArgument = parseShortOptionWithArgument( candidate );

        if ( isRecognized( optionAndArgument.key ) ) {
            specFor( optionAndArgument.key ).handleOption( this, arguments, detected, optionAndArgument.value );
        } else
            handleShortOptionCluster( candidate, arguments, detected );
    }

    private void handleShortOptionCluster( String candidate, ArgumentList arguments, OptionSet detected ) {
        char[] options = extractShortOptionsFrom( candidate );
        validateOptionCharacters( options );

        for ( int i = 0; i < options.length; i++ ) {
            AbstractOptionSpec<?> optionSpec = specFor( options[i] );

            if ( optionSpec.acceptsArguments() && options.length > i + 1 ) {
                String detectedArgument = String.valueOf( options, i + 1, options.length - 1 - i );
                optionSpec.handleOption( this, arguments, detected, detectedArgument );
                break;
            }

            optionSpec.handleOption( this, arguments, detected, null );
        }
    }

    void handleNonOptionArgument( String candidate, ArgumentList arguments, OptionSet detectedOptions ) {
        specFor( NonOptionArgumentSpec.NAME ).handleOption( this, arguments, detectedOptions, candidate );
    }

    void noMoreOptions() {
        state = OptionParserState.noMoreOptions();
    }

    boolean looksLikeAnOption( String argument ) {
        return isShortOptionToken( argument ) || isLongOptionToken( argument );
    }

    boolean isRecognized( String option ) {
        return recognizedOptions.contains( option );
    }

    void requiredIf( Collection<String> precedentSynonyms, String required ) {
        requiredIf( precedentSynonyms, specFor( required ) );
    }

    void requiredIf( Collection<String> precedentSynonyms, OptionSpec<?> required ) {
        putRequiredOption( precedentSynonyms, required, requiredIf );
    }

    void requiredUnless( Collection<String> precedentSynonyms, String required ) {
        requiredUnless( precedentSynonyms, specFor( required ) );
    }

    void requiredUnless( Collection<String> precedentSynonyms, OptionSpec<?> required ) {
        putRequiredOption( precedentSynonyms, required, requiredUnless );
    }

    private void putRequiredOption( Collection<String> precedentSynonyms, OptionSpec<?> required,
        Map<Collection<String>, Set<OptionSpec<?>>> target ) {

        for ( String each : precedentSynonyms ) {
            AbstractOptionSpec<?> spec = specFor( each );
            if ( spec == null )
                throw new UnconfiguredOptionException( precedentSynonyms );
        }

        Set<OptionSpec<?>> associated = target.get( precedentSynonyms );
        if ( associated == null ) {
            associated = new HashSet<OptionSpec<?>>();
            target.put( precedentSynonyms, associated );
        }

        associated.add( required );
    }

    private AbstractOptionSpec<?> specFor( char option ) {
        return specFor( String.valueOf( option ) );
    }

    private AbstractOptionSpec<?> specFor( String option ) {
        return recognizedOptions.get( option );
    }

    private void reset() {
        state = moreOptions( posixlyCorrect );
    }

    private static char[] extractShortOptionsFrom( String argument ) {
        char[] options = new char[argument.length() - 1];
        argument.getChars( 1, argument.length(), options, 0 );

        return options;
    }

    private void validateOptionCharacters( char[] options ) {
        for ( char each : options ) {
            String option = String.valueOf( each );

            if ( !isRecognized( option ) )
                throw unrecognizedOption( option );

            if ( specFor( option ).acceptsArguments() )
                return;
        }
    }

    private static KeyValuePair parseLongOptionWithArgument( String argument ) {
        return KeyValuePair.valueOf( argument.substring( 2 ) );
    }

    private static KeyValuePair parseShortOptionWithArgument( String argument ) {
        return KeyValuePair.valueOf( argument.substring( 1 ) );
    }
}
