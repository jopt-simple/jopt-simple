package joptsimple.util;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

/**
 * converts values case insensitively matching T, True, F, or False to {@link java.lang.Boolean}
 * throws {@link joptsimple.ValueConversionException} otherwise
 * @author <a href="mailto:louisb@broadinstitute.org">Louis Bergelson</a>
 */
public class StrictBooleanConverter implements ValueConverter<Boolean> {
        public Boolean convert( String value ) {
            if ( value.equalsIgnoreCase("true") || value.equalsIgnoreCase("t")) {
                return true;
            } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("f")) {
                return false;
            } else {
                throw new ValueConversionException(value + " does not match one of T|True|F|False");
            }
        }
        public Class<? extends Boolean> valueType() {
            return boolean.class;
        }

    public String valuePattern() {
        return "[T|True|F|False]";
    }
}
