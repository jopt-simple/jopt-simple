package joptsimple;

import java.io.File;

import org.junit.Test;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;

public class ParsingWithSubcommandsTest extends AbstractOptionParserFixture {
    @Test
    public void oneSubcommandWithOptions() {
        parser.acceptsAll(asList("v", "verbose"));
        OptionDeclarer list = parser.acceptsSubcommand("list");
        list.accepts("f", "file")
            .withRequiredArg()
            .ofType(File.class);

        OptionSet options =
            parser.parse(
                "--verbose",
                "list",
                "--file",
                "/tmp",
                "arg1",
                "arg2");

        assertNull(options.name());
        assertTrue(options.has("v"));
        assertEquals(emptyList(), options.nonOptionArguments());

        OptionSet subcommand = options.subcommand();
        assertEquals("list", subcommand.name());
        assertTrue(subcommand.has("file"));
        assertEquals(new File("/tmp"), subcommand.valueOf("file"));
        assertEquals(asList("arg1", "arg2"), subcommand.nonOptionArguments());
    }
}
