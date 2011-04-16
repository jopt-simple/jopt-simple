package joptsimple;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.infinitest.toolkit.CollectionMatchers;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.*;
import static org.infinitest.toolkit.CollectionMatchers.*;
import static org.junit.Assert.*;

public class OptionParserAlternateHelpFormatterTest extends AbstractOptionParserFixture {
    private StringWriter sink;
    private Collection<OptionDescriptor> captured;

    @Before
    public void primeParser() throws Exception {
        captured = new ArrayList<OptionDescriptor>();

        parser.accepts("b", "boo");

        parser.formatsHelpWith(new HelpFormatter() {
            public String format(Collection<? extends OptionDescriptor> options) {
                captured.addAll(options);
                return "some help you are";
            }
        });

        sink = new StringWriter();

        parser.printHelpOn(sink);
    }

    @Test
    public void asksAlternateFormatterForHelpString() {
        assertEquals("some help you are", sink.toString());
    }

    @Test
    public void getsFedOptionDescriptorsForRecognizedOptions() {
        assertEquals(1, captured.size());
        OptionDescriptor only = captured.iterator().next();
        assertThat(only.options(), hasSameContentsAs(asList("b")));
        assertEquals("boo", only.description());
        assertFalse(only.acceptsArguments());
        assertFalse(only.requiresArgument());
        assertEquals("", only.argumentDescription());
        assertEquals("", only.argumentTypeIndicator());
        assertEquals(Collections.<Object> emptyList(), only.defaultValues());
    }
}
