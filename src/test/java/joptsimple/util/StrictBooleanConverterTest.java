package joptsimple.util;

import joptsimple.ValueConversionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
* @author <a href="mailto:louisb@broadinstitute.org">Louis Bergelson</a> */
public class StrictBooleanConverterTest {

    @Test
    public void recognizedValues(){
        StrictBooleanConverter converter = new StrictBooleanConverter();
        assertTrue(converter.convert("true"));
        assertTrue(converter.convert("T"));
        assertTrue(converter.convert("TRUE"));
        assertFalse(converter.convert("F"));
        assertFalse(converter.convert("False"));
    }

    @Test( expected = ValueConversionException.class)
    public void unrecognizedValues(){
        StrictBooleanConverter converter = new StrictBooleanConverter();
        converter.convert("unprovable");
    }

}
