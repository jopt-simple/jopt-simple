package joptsimple;

import java.util.Collection;

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
 *   OptionParser#OptionParser(String) constructor}. Arguments of options specified in this manner will be of type
 *   {@link String}. Here are the rules for the format of the specification strings this constructor accepts:
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
 *   <li>using the {@linkplain OptionParser#OptionParser(String) constructor} with an argument whose first character
 *   is a plus sign (<kbd>"+"</kbd>)</li>
 * </ol>
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 * @see <a href="http://www.gnu.org/software/libc/manual">The GNU C Library</a>
 */
public interface OptionDeclarer {
    /**
     * <p>Tells the parser to recognize the given option.</p>
     *
     * <p>This method returns an instance of {@link OptionSpecBuilder} to allow the formation of parser directives
     * as sentences in a fluent interface language. For example:</p>
     *
     * <pre><code>
     *   OptionParser parser = new OptionParser();
     *   parser.<strong>accepts( "c" )</strong>.withRequiredArg().ofType( Integer.class );
     * </code></pre>
     *
     * <p>If no methods are invoked on the returned {@link OptionSpecBuilder}, then the parser treats the option as
     * accepting no argument.</p>
     *
     * @param option the option to recognize
     * @return an object that can be used to flesh out more detail about the option
     * @throws OptionException if the option contains illegal characters
     * @throws NullPointerException if the option is {@code null}
     */
    OptionSpecBuilder accepts(String option);

    /**
     * Tells the parser to recognize the given option.
     *
     * @see #accepts(String)
     * @param option the option to recognize
     * @param description a string that describes the purpose of the option. This is used when generating help
     * information about the parser.
     * @return an object that can be used to flesh out more detail about the option
     * @throws OptionException if the option contains illegal characters
     * @throws NullPointerException if the option is {@code null}
     */
    OptionSpecBuilder accepts(String option, String description);

    /**
     * Tells the parser to recognize the given options, and treat them as synonymous.
     *
     * @see #accepts(String)
     * @param options the options to recognize and treat as synonymous
     * @return an object that can be used to flesh out more detail about the options
     * @throws OptionException if any of the options contain illegal characters
     * @throws NullPointerException if the option list or any of its elements are {@code null}
     */
    OptionSpecBuilder acceptsAll(Collection<String> options);

    /**
     * Tells the parser to recognize the given options, and treat them as synonymous.
     *
     * @see #acceptsAll(Collection)
     * @param options the options to recognize and treat as synonymous
     * @param description a string that describes the purpose of the option.  This is used when generating help
     * information about the parser.
     * @return an object that can be used to flesh out more detail about the options
     * @throws OptionException if any of the options contain illegal characters
     * @throws NullPointerException if the option list or any of its elements are {@code null}
     * @throws IllegalArgumentException if the option list is empty
     */
    OptionSpecBuilder acceptsAll(Collection<String> options, String description);

    /**
     * Gives an object that represents an access point for non-option arguments on a command line.
     *
     * @return an object that can be used to flesh out more detail about the non-option arguments
     */
    NonOptionArgumentSpec<String> nonOptions();

    /**
     * Gives an object that represents an access point for non-option arguments on a command line.
     *
     * @see #nonOptions()
     * @param description a string that describes the purpose of the non-option arguments. This is used when generating
     * help information about the parser.
     * @return an object that can be used to flesh out more detail about the non-option arguments
     */
    NonOptionArgumentSpec<String> nonOptions(String description);

    /**
     * Tells the parser whether or not to behave "POSIX-ly correct"-ly.
     *
     * @param setting {@code true} if the parser should behave "POSIX-ly correct"-ly
     */
    void posixlyCorrect(boolean setting);

    /**
     * <p>Tells the parser to treat unrecognized options as non-option arguments.</p>
     *
     * <p>If not called, then the parser raises an {@link OptionException} when it encounters an unrecognized
     * option.</p>
     */
    void allowsUnrecognizedOptions();

    /**
     * Tells the parser either to recognize or ignore <kbd>"-W"</kbd>-style long options.
     *
     * @param recognize {@code true} if the parser is to recognize the special style of long options
     */
    void recognizeAlternativeLongOptions(boolean recognize);
}
