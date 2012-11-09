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

package joptsimple;

import joptsimple.internal.Strings;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.*;
import static joptsimple.ParserRules.*;
import static joptsimple.internal.Classes.*;
import static joptsimple.internal.Strings.*;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class ConfigurableHelpFormatter implements HelpFormatter {

    private final Set<Row> rows = new LinkedHashSet<Row>();

    private int optionWidth = 0;
    private int descriptionWidth = 0;

    public String format( Map<String, ? extends OptionDescriptor> options ) {
        if ( options.isEmpty() )
            return "No options specified";

        addRows(options.values());

        return renderRows(rows);
    }

    private void addRows( Collection<? extends OptionDescriptor> options ) {
        addHeaders(options);
        addOptions(options);
    }

    private void addHeaders( Collection<? extends OptionDescriptor> options ) {
        if ( !hasRequiredOption(options) ) {
            addRow("Option", "Description");
            addRow("------", "-----------");
        } else {
            addRow("Option (* = required)", "Description");
            addRow("---------------------", "-----------");
        }
    }

    private boolean hasRequiredOption( Collection<? extends OptionDescriptor> options ) {
        for ( OptionDescriptor each : options ) {
            if ( each.isRequired() ) {
                return true;
            }
        }
        return false;
    }

    private void addOptions( Collection<? extends OptionDescriptor> options ) {
        for ( OptionDescriptor each : options ) {
            String option = createOptionDisplay(each);
            String description = createDescriptionDisplay(each);
            addRow(option, description);
        }
    }

    private String createOptionDisplay( OptionDescriptor descriptor ) {
        StringBuilder buffer = new StringBuilder( descriptor.isRequired() ? "* " : "" );

        for ( Iterator<String> i = descriptor.options().iterator(); i.hasNext(); ) {
            String option = i.next();
            buffer.append(option.length() > 1 ? DOUBLE_HYPHEN : HYPHEN);
            buffer.append( option );

            if ( i.hasNext() )
                buffer.append( ", " );
        }

        maybeAppendOptionInfo(buffer, descriptor);

        return buffer.toString();
    }

    private void maybeAppendOptionInfo( StringBuilder buffer, OptionDescriptor descriptor ) {
        String indicator = extractTypeIndicator(descriptor);
        String description = descriptor.argumentDescription();
        if ( indicator != null || !isNullOrEmpty(description)) {
            appendOptionHelp(buffer, indicator, description, descriptor.requiresArgument());
        }
    }

    private String extractTypeIndicator( OptionDescriptor descriptor ) {
        String indicator = descriptor.argumentTypeIndicator();
        if ( !isNullOrEmpty(indicator) && !String.class.getName().equals(indicator) ) {
            return shortNameOf(indicator);
        }
        return null;
    }

    private void appendOptionHelp( StringBuilder buffer, String typeIndicator, String description, boolean required ) {
        if ( required ) {
            appendTypeIndicator(buffer, typeIndicator, description, '<', '>');
        } else {
            appendTypeIndicator(buffer, typeIndicator, description, '[', ']');
        }
    }

    private void appendTypeIndicator( StringBuilder buffer, String typeIndicator, String description,
                                      char start, char end ) {

        buffer.append(" ").append(start);
        if ( typeIndicator != null) {
            buffer.append(typeIndicator);
        }

        if ( !Strings.isNullOrEmpty(description) ) {
            if ( typeIndicator != null ) {
                buffer.append(": ");
            }
            buffer.append(description);
        }
        buffer.append(end);
    }

    private String createDescriptionDisplay( OptionDescriptor descriptor ) {
        List<?> defaultValues = descriptor.defaultValues();
        if ( defaultValues.isEmpty() )
            return descriptor.description();

        String defaultValuesDisplay = createDefaultValuesDisplay( defaultValues );
        return (descriptor.description() + ' ' + surround( "default: " + defaultValuesDisplay, '(', ')' )).trim();
    }

    private String createDefaultValuesDisplay( List<?> defaultValues ) {
        return defaultValues.size() == 1 ? defaultValues.get( 0 ).toString() : defaultValues.toString();
    }

    private String renderRows( Set<Row> rows ) {
        StringBuilder buffer = new StringBuilder();
        for ( Row row : rows ) {
            pad(buffer, row.option, optionWidth).append("  ");
            pad(buffer, row.description, descriptionWidth).append("\n");
        }
        return buffer.toString();
    }

    private void addRow( String option, String description ) {
        rows.add(new Row(option, description));
        optionWidth = max(optionWidth, option.length());
        descriptionWidth = max(descriptionWidth, description.length());
    }

    private StringBuilder pad( StringBuilder buffer, String s, int length ) {
        buffer.append(s);
        for (int i = s.length(); i < length; i++)
            buffer.append(" ");
        return buffer;
    }

    private static class Row {
        private final String option;
        private final String description;

        private Row( String option, String description ) {
            this.option = option;
            this.description = description;
        }

        @Override
        public int hashCode() {
            return option.hashCode() * 17 + description.hashCode();
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }

            if ( !(o instanceof Row) ) {
                return false;
            }

            Row that = (Row) o;

            return option.equals(that.option) && description.equals(that.description);
        }
    }

}
