package joptsimple;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.*;

import org.junit.Before;
import org.junit.Test;

import static org.infinitest.toolkit.CollectionMatchers.*;
import static org.junit.Assert.*;

public class OptionParserAlternateHelpFormatterTest extends AbstractOptionParserFixture {
    private StringWriter sink;
    private Map<String, ? extends OptionDescriptor> captured;

    @Before
    public void primeParser() throws Exception {
        captured = new HashMap<String, OptionDescriptor>();

        parser.accepts( "b", "boo" );

        parser.formatHelpWith(new HelpFormatter() {
            public String format( Map<String, ? extends OptionDescriptor> options ) {
                captured = options;
                return "some help you are";
            }
        });

        sink = new StringWriter();

        parser.printHelpOn( sink );
    }

    @Test
    public void asksAlternateFormatterForHelpString() {
        assertEquals( "some help you are", sink.toString() );
    }

    @Test
    public void getsFedOptionDescriptorsForRecognizedOptions() {
        assertEquals( 1, captured.size() );
        Map.Entry<String, ? extends OptionDescriptor> only = captured.entrySet().iterator().next();
        assertEquals( "b", only.getKey() );
        OptionDescriptor descriptor = only.getValue();
        assertThat( descriptor.options(), hasSameContentsAs( asList( "b" ) ) );
        assertEquals( "boo", descriptor.description() );
        assertFalse( descriptor.acceptsArguments() );
        assertFalse( descriptor.requiresArgument() );
        assertEquals( "", descriptor.argumentDescription() );
        assertEquals( "", descriptor.argumentTypeIndicator() );
        assertEquals( Collections.<Object> emptyList(), descriptor.defaultValues() );
    }
}
