/*
 The MIT License

 Copyright (c) 2004-2014 Paul R. Holser, Jr.

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

package net.sf.joptsimple.converter;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.function.Function;

import net.sf.joptsimple.ValueConversionException;
import net.sf.joptsimple.ValueConverter;

/**
 * Converts values to {@link java.lang.Enum}s.
 *
 * @author <a href="mailto:christian.ohr@gmail.com">Christian Ohr</a>
 */
public final class EnumConverter<E extends Enum<E>> implements ValueConverter<E> {
    private final Class<E> clazz;

    private String delimiters = "[,]";
    private Function<E, String> nameMapper = Enum::name;

    private EnumConverter(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E convert( String value ) {
        for ( E each : valueType().getEnumConstants() ) {
            if ( nameMapper.apply( each ).equalsIgnoreCase( value ) ) {
                return each;
            }
        }

        throw new ValueConversionException( message( value ) );
    }

    @Override
    public String revert( E value ) {
        return nameMapper.apply( value );
    }

    @Override
    public Class<E> valueType() {
        return clazz;
    }

    /**
     * Sets the delimiters for the message string. Must be a 3-letter string, where
     * the first character is the prefix, the second character is the delimiter
     * between the values, and the 3rd character is the suffix.
     *
     * @param delimiters delimiters for message string. Default is [,]
     */
    public void setDelimiters( String delimiters ) {
        this.delimiters = delimiters;
    }

    /**
     * Sets the mapper that maps enum constant to its string representation.
     * {@code null} resets name mapper to {@link Enum#name() the default mapper}
     *
     * @param nameMapper mapper for obtaining an enum's string representation.
     *                   Default is {@link Enum#name()}
     */
    public void setNameMapper( Function<E, String> nameMapper ) {
        if (nameMapper == null) {
            nameMapper = Enum::name;
        }
        this.nameMapper = nameMapper;
    }

    @Override
    public String valuePattern() {
        EnumSet<E> values = EnumSet.allOf( valueType() );

        StringBuilder builder = new StringBuilder();
        builder.append( delimiters.charAt( 0 ) );
        for ( Iterator<E> i = values.iterator(); i.hasNext(); ) {
            builder.append( nameMapper.apply( i.next() ) );
            if ( i.hasNext() )
                builder.append( delimiters.charAt( 1 ) );
        }
        builder.append( delimiters.charAt( 2 ) );

        return builder.toString();
    }

    private String message( String value ) {
        ResourceBundle bundle = ResourceBundle.getBundle( "net.sf.joptsimple.ExceptionMessages" );
        Object[] arguments = new Object[] { value, valuePattern() };
        String template = bundle.getString( EnumConverter.class.getName() + ".message" );
        return new MessageFormat( template ).format( arguments );
    }

    public static <E extends Enum<E>> EnumConverter<E> of( Class<E> enumType ) {
        return new EnumConverter<>( enumType );
    }
}
