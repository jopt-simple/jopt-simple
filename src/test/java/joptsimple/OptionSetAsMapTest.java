package joptsimple;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@code OptionSetAsMapTest} needs documentation.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @todo Needs documentation.
 */
public class OptionSetAsMapTest extends AbstractOptionParserFixture {
    @Test
    public void gives() {
        final OptionSpec<Void> a = parser.accepts( "a" );
        final OptionSpec<String> b = parser.accepts( "b" ).withRequiredArg();
        final OptionSpec<String> c = parser.accepts( "c" ).withOptionalArg();
        final OptionSpec<String> d = parser.accepts( "d" ).withRequiredArg().defaultsTo( "1" );
        final OptionSpec<String> e = parser.accepts( "e" ).withOptionalArg().defaultsTo( "2" );
        final OptionSpec<String> f = parser.accepts( "f" ).withRequiredArg().defaultsTo( "3" );
        final OptionSpec<String> g = parser.accepts( "g" ).withOptionalArg().defaultsTo( "4" );

        OptionSet options = parser.parse( "-a", "-e", "-c", "5", "-d", "6", "-b", "4", "-d", "7", "-e", "8" );

        final Map<OptionSpec<?>, List<?>> expected = new HashMap<OptionSpec<?>, List<?>>() {
            {
                put( a, asList() );
                put( b, asList( "4" ) );
                put( c, asList( "5" ) );
                put( d, asList( "6", "7" ) );
                put( e, asList( "8" ) );
                put( f, asList( "3" ) );
                put( g, asList( "4" ) );
            }
        };
        assertThat( options.asMap(), is( equalTo( expected ) ) );
    }
}
