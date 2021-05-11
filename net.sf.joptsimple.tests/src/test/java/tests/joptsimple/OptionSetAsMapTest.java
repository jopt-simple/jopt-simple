package tests.joptsimple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.*;
import static java.util.Collections.*;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
public class OptionSetAsMapTest extends AbstractOptionParserFixture {
    @Test
    public void gives() {
        OptionSpec<Void> a = parser.accepts( "a" );
        OptionSpec<String> b = parser.accepts( "b" ).withRequiredArg();
        OptionSpec<String> c = parser.accepts( "c" ).withOptionalArg();
        OptionSpec<String> d = parser.accepts( "d" ).withRequiredArg().defaultsTo( "1" );
        OptionSpec<String> e = parser.accepts( "e" ).withOptionalArg().defaultsTo( "2" );
        OptionSpec<String> f = parser.accepts( "f" ).withRequiredArg().defaultsTo( "3" );
        OptionSpec<String> g = parser.accepts( "g" ).withOptionalArg().defaultsTo( "4" );
        OptionSpec<Void> h = parser.accepts( "h" );

        OptionSet options = parser.parse( "-a", "-e", "-c", "5", "-d", "6", "-b", "4", "-d", "7", "-e", "8" );

        Map<OptionSpec<?>, List<?>> expected = new HashMap<OptionSpec<?>, List<?>>() {
            private static final long serialVersionUID = Long.MIN_VALUE;

            {
                put( a, emptyList() );
                put( b, singletonList( "4" ) );
                put( c, singletonList( "5" ) );
                put( d, asList( "6", "7" ) );
                put( e, singletonList( "8" ) );
                put( f, singletonList( "3" ) );
                put( g, singletonList( "4" ) );
                put( h, emptyList() );
            }
        };

        assertEquals( expected, options.asMap() );
    }
}
