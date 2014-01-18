package joptsimple;

import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.infinitest.toolkit.CollectionMatchers.hasSameContentsAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class OptionParserAlternateHelpFormatterTest extends AbstractOptionParserFixture {
    private StringWriter sink;
    private Map<String, ? extends OptionDescriptor> captured;

    @Before
    public void primeParser() throws Exception {
        captured = new HashMap<String, OptionDescriptor>();

        parser.accepts( "b", "boo" );

        parser.formatHelpWith(new HelpFormatter() {
            public String format( final OptionParser optionParser ) {
                captured = optionParser.recognizedOptions();
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
        assertEquals( 2, captured.size() );
        Iterator<? extends Map.Entry<String,? extends OptionDescriptor>> iterator = captured.entrySet().iterator();
        Map.Entry<String, ? extends OptionDescriptor> first = iterator.next();
        assertEquals( "[arguments]", first.getKey() );
        Map.Entry<String, ? extends OptionDescriptor> second = iterator.next();
        assertEquals("b", second.getKey());
        OptionDescriptor descriptor = second.getValue();
        assertThat( descriptor.options(), hasSameContentsAs( asList( "b" ) ) );
        assertEquals( "boo", descriptor.description() );
        assertFalse( descriptor.acceptsArguments() );
        assertFalse( descriptor.requiresArgument() );
        assertEquals( "", descriptor.argumentDescription() );
        assertEquals( "", descriptor.argumentTypeIndicator() );
        assertEquals( Collections.<Object> emptyList(), descriptor.defaultValues() );
    }
}
