/*
 The MIT License

 Copyright (c) 2004-2011 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package joptsimple.util;

import java.text.DateFormat;
import static java.text.DateFormat.*;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import static joptsimple.util.DateConverter.*;
import static org.hamcrest.CoreMatchers.*;
import org.joda.time.DateMidnight;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.*;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class DateConverterTest {
    private DateFormat notASimpleDateFormat;
    private SimpleDateFormat monthDayYear;

    @Before
    public void setUp() {
        notASimpleDateFormat = new DateFormat() {
            private static final long serialVersionUID = -1L;

            {
                setNumberFormat( NumberFormat.getInstance() );
            }

            @Override
            public StringBuffer format( Date date, StringBuffer toAppendTo, FieldPosition fieldPosition ) {
                return null;
            }

            @Override
            public Date parse( String source, ParsePosition pos ) {
                return null;
            }
        };

        monthDayYear = new SimpleDateFormat( "MM/dd/yyyy" );
    }

    @Test( expected = NullPointerException.class )
    public void shouldRejectNullDateFormatter() {
        new DateConverter( null );
    }

    @Test
    public void shouldConvertValuesToDatesUsingADateFormat() {
        ValueConverter<Date> converter = new DateConverter( monthDayYear );

        assertEquals( new DateMidnight( 2009, 1, 24 ).toDate(), converter.convert( "01/24/2009" ) );
    }

    @Test( expected = ValueConversionException.class )
    public void shouldRejectNonParsableValues() {
        new DateConverter( getDateInstance() ).convert( "@(#*^" );
    }

    @Test( expected = ValueConversionException.class )
    public void shouldRejectValuesThatDoNotEntirelyMatch() {
        new DateConverter( monthDayYear ).convert( "12/25/09 00:00:00" );
    }

    @Test
    public void shouldCreateSimpleDateFormatConverter() {
        assertEquals( new DateMidnight( 2009, 7, 4 ).toDate(), datePattern( "MM/dd/yyyy" ).convert( "07/04/2009" ) );
    }

    @Test( expected = NullPointerException.class )
    public void shouldRejectNullDatePattern() {
        datePattern( null );
    }

    @Test
    public void shouldRaiseExceptionThatContainsDatePatternAndValue() {
        try {
            new DateConverter( monthDayYear ).convert( "qwe" );
            fail();
        }
        catch ( ValueConversionException expected ) {
            assertThat( expected.getMessage(), containsString( "qwe" ) );
            assertThat( expected.getMessage(), containsString( monthDayYear.toLocalizedPattern() ) );
        }
    }

    @Test
    public void shouldRaiseExceptionThatContainsValueOnlyIfNotASimpleDateFormat() {
        try {
            new DateConverter( notASimpleDateFormat ).convert( "asdf" );
            fail();
        }
        catch ( ValueConversionException expected ) {
            assertThat( expected.getMessage(), containsString( "asdf" ) );
            assertThat( expected.getMessage(), not( containsString( notASimpleDateFormat.toString() ) ) );
        }
    }

    @Test
    public void shouldAnswerCorrectValueType() {
        assertSame( Date.class, new DateConverter( monthDayYear ).valueType() );
    }

    @Test
    public void shouldGiveNoValuePatternIfFormatterNotASimpleDateFormat() {
        assertEquals( "", new DateConverter( notASimpleDateFormat ).valuePattern() );
    }

    @Test
    public void shouldGiveValuePatternIfFormatterIsASimpleDateFormat() {
        assertEquals(
            monthDayYear.toLocalizedPattern(),
            datePattern( monthDayYear.toLocalizedPattern() ).valuePattern() );
    }
}
