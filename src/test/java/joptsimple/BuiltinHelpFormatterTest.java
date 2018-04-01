package joptsimple;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import joptsimple.util.DateConverter;

public class BuiltinHelpFormatterTest {

    @Test //for issue #127
    public void testFormatHelpWithDateOptionAndPatternThatContainsDots() throws Exception {
        NonOptionArgumentSpec<String> nonOptionArgumentSpec = new NonOptionArgumentSpec<>();

        ArgumentAcceptingOptionSpec<Date> optionSpec = new RequiredArgumentOptionSpec<Date>( "date" )
            .withValuesConvertedBy( DateConverter.datePattern( "dd.MM.yyyy" ) );

        HashMap<String, OptionDescriptor> options = new HashMap<>();

        options.put( "none", nonOptionArgumentSpec );
        options.put( "date", optionSpec );

        BuiltinHelpFormatter builtinHelpFormatter = new BuiltinHelpFormatter();

        String actual = builtinHelpFormatter.format( options );

        String expected =
            "Option               Description\n" +
                "------               -----------\n" +
                "--date <dd.MM.yyyy>             \n";

        assertEquals( expected, actual );
    }

}