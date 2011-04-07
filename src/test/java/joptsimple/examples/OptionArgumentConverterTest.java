package joptsimple.examples;

import static joptsimple.util.DateConverter.*;
import static joptsimple.util.RegexMatcher.*;
import static org.junit.Assert.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.joda.time.DateMidnight;
import org.junit.Test;

public class OptionArgumentConverterTest {
    @Test
    public void shouldUseConvertersOnOptionArgumentsWhenTold() {
        OptionParser parser = new OptionParser();
        parser.accepts( "birthdate" ).withRequiredArg().withValuesConvertedBy( datePattern( "MM/dd/yy" ) );
        parser.accepts( "ssn" ).withRequiredArg().withValuesConvertedBy( regex( "\\d{3}-\\d{2}-\\d{4}" ));

        OptionSet options = parser.parse( "--birthdate", "02/24/05", "--ssn", "123-45-6789" );

        assertEquals( new DateMidnight( 2005, 2, 24 ).toDate(), options.valueOf( "birthdate" ) );
        assertEquals( "123-45-6789", options.valueOf( "ssn" ) );
    }
}
