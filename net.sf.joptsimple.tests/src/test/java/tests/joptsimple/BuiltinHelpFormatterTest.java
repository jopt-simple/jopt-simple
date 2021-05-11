package tests.joptsimple;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import joptsimple.AbstractOptionSpec;
import joptsimple.BuiltinHelpFormatter;
import joptsimple.OptionParser;
import org.junit.Test;

import joptsimple.util.DateConverter;

public class BuiltinHelpFormatterTest {
    @Test //for issue #127
    public void gh127FormatHelpWithDateOptionAndPatternThatContainsDots() {
        OptionParser parser = new OptionParser();
        parser.accepts( "date" ).withRequiredArg().ofType( Date.class )
            .withValuesConvertedBy( DateConverter.datePattern( "dd.MM.yyyy" ) );

        Map<String, AbstractOptionSpec<?>> specs = parser.recognizedOptions();

        BuiltinHelpFormatter builtinHelpFormatter = new BuiltinHelpFormatter();

        String actual = builtinHelpFormatter.format( specs );

        String expected =
            "Option               Description\n" +
                "------               -----------\n" +
                "--date <dd.MM.yyyy>             \n";

        assertEquals( expected, actual );
    }
}
