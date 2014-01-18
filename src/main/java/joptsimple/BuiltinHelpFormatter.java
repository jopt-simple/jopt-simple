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

package joptsimple;

import joptsimple.internal.Rows;
import joptsimple.internal.Strings;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static joptsimple.OptionOrder.FIRST_OPTION_ORDER;
import static joptsimple.ParserRules.DOUBLE_HYPHEN;
import static joptsimple.ParserRules.HYPHEN;
import static joptsimple.internal.Classes.shortNameOf;
import static joptsimple.internal.Strings.LINE_SEPARATOR;
import static joptsimple.internal.Strings.isNullOrEmpty;
import static joptsimple.internal.Strings.surround;

/**
 * <p>A help formatter that allows configuration of overall row width and column separator width.</p>
 *
 * <p>The formatter produces a two-column output. The left column is for the options, and the right column for their
 * descriptions. The formatter will allow as much space as possible for the descriptions, by minimizing the option
 * column's width, no greater than slightly less than half the overall desired width.</p>
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class BuiltinHelpFormatter implements HelpFormatter {
    private final Rows nonOptionRows;
    private final Rows optionRows;
    private final OptionOrder optionOrder;

    /**
     * Makes a formatter with a pre-configured overall row width and column separator width.
     */
    BuiltinHelpFormatter() {
        this( 80, 2, FIRST_OPTION_ORDER);
    }

    /**
     * Makes a formatter with a given overall row width and column separator width.
     *
     * @param desiredOverallWidth how many characters wide to make the overall help display
     * @param desiredColumnSeparatorWidth how many characters wide to make the separation between option column and
     * description column
     */
    public BuiltinHelpFormatter( int desiredOverallWidth, int desiredColumnSeparatorWidth,
            OptionOrder optionOrder ) {
        nonOptionRows = new Rows( desiredOverallWidth * 2, 0 );
        optionRows = new Rows( desiredOverallWidth, desiredColumnSeparatorWidth );
        this.optionOrder = optionOrder;
    }

    public String format( OptionParser optionParser ) {
        addRows( optionOrder.of( optionParser ) );
        return formattedHelpOutput();
    }

    private String formattedHelpOutput() {
        StringBuilder formatted = new StringBuilder();
        String nonOptionDisplay = nonOptionRows.render();
        if ( !Strings.isNullOrEmpty( nonOptionDisplay ) )
            formatted.append( nonOptionDisplay ).append( LINE_SEPARATOR );
        formatted.append( optionRows.render() );

        return formatted.toString();
    }

    private void addRows( Collection<? extends OptionDescriptor> options ) {
        addNonOptionsDescription( options );

        if ( options.isEmpty() )
            optionRows.add( "No options specified", "" );
        else {
            addHeaders( options );
            addOptions( options );
        }

        fitRowsToWidth();
    }

    private void addNonOptionsDescription( Collection<? extends OptionDescriptor> options ) {
        OptionDescriptor nonOptions = findAndRemoveNonOptionsSpec( options );
        if ( shouldShowNonOptionArgumentDisplay( nonOptions ) ) {
            nonOptionRows.add( "Non-option arguments:", "" );
            nonOptionRows.add(createNonOptionArgumentsDisplay(nonOptions), "");
        }
    }

    private boolean shouldShowNonOptionArgumentDisplay( OptionDescriptor nonOptions ) {
        return !Strings.isNullOrEmpty( nonOptions.description() )
            || !Strings.isNullOrEmpty( nonOptions.argumentTypeIndicator() )
            || !Strings.isNullOrEmpty( nonOptions.argumentDescription() );
    }

    private String createNonOptionArgumentsDisplay( OptionDescriptor nonOptions) {
        StringBuilder buffer = new StringBuilder();
        maybeAppendOptionInfo( buffer, nonOptions );
        maybeAppendNonOptionsDescription( buffer, nonOptions );

        return buffer.toString();
    }

    private void maybeAppendNonOptionsDescription( StringBuilder buffer, OptionDescriptor nonOptions ) {
        buffer.append( buffer.length() > 0 && !Strings.isNullOrEmpty( nonOptions.description() ) ? " -- " : "" )
            .append( nonOptions.description() );
    }

    private OptionDescriptor findAndRemoveNonOptionsSpec( Collection<? extends OptionDescriptor> options ) {
        for ( Iterator<? extends OptionDescriptor> it = options.iterator(); it.hasNext(); ) {
            OptionDescriptor next = it.next();
            if ( next.representsNonOptions() ) {
                it.remove();
                return next;
            }
        }

        throw new AssertionError( "no non-options argument spec" );
    }

    private void addHeaders( Collection<? extends OptionDescriptor> options ) {
        if ( hasRequiredOption( options ) ) {
            optionRows.add("Option (* = required)", "Description");
            optionRows.add("---------------------", "-----------");
        } else {
            optionRows.add("Option", "Description");
            optionRows.add("------", "-----------");
        }
    }

    private boolean hasRequiredOption( Collection<? extends OptionDescriptor> options ) {
        for ( OptionDescriptor each : options ) {
            if ( each.isRequired() )
                return true;
        }

        return false;
    }

    private void addOptions( Collection<? extends OptionDescriptor> options ) {
        for ( OptionDescriptor each : options ) {
            if ( !each.representsNonOptions() )
                optionRows.add( createOptionDisplay( each ), createDescriptionDisplay( each ) );
        }
    }

    private String createOptionDisplay( OptionDescriptor descriptor ) {
        StringBuilder buffer = new StringBuilder( descriptor.isRequired() ? "* " : "" );

        for ( Iterator<String> i = descriptor.options().iterator(); i.hasNext(); ) {
            String option = i.next();
            buffer.append( option.length() > 1 ? DOUBLE_HYPHEN : HYPHEN );
            buffer.append( option );

            if ( i.hasNext() )
                buffer.append( ", " );
        }

        maybeAppendOptionInfo( buffer, descriptor );

        return buffer.toString();
    }

    private void maybeAppendOptionInfo( StringBuilder buffer, OptionDescriptor descriptor ) {
        String indicator = extractTypeIndicator( descriptor );
        String description = descriptor.argumentDescription();
        if ( indicator != null || !isNullOrEmpty( description ) )
            appendOptionHelp( buffer, indicator, description, descriptor.requiresArgument() );
    }

    private String extractTypeIndicator( OptionDescriptor descriptor ) {
        String indicator = descriptor.argumentTypeIndicator();

        if ( !isNullOrEmpty( indicator ) && !String.class.getName().equals( indicator ) )
            return shortNameOf( indicator );

        return null;
    }

    private void appendOptionHelp( StringBuilder buffer, String typeIndicator, String description, boolean required ) {
        if ( required )
            appendTypeIndicator( buffer, typeIndicator, description, '<', '>' );
        else
            appendTypeIndicator( buffer, typeIndicator, description, '[', ']' );
    }

    private void appendTypeIndicator( StringBuilder buffer, String typeIndicator, String description,
                                      char start, char end ) {
        buffer.append( ' ' ).append( start );
        if ( typeIndicator != null )
            buffer.append( typeIndicator );

        if ( !Strings.isNullOrEmpty( description ) ) {
            if ( typeIndicator != null )
                buffer.append( ": " );

            buffer.append( description );
        }

        buffer.append( end );
    }

    private String createDescriptionDisplay( OptionDescriptor descriptor ) {
        List<?> defaultValues = descriptor.defaultValues();
        if ( defaultValues.isEmpty() )
            return descriptor.description();

        String defaultValuesDisplay = createDefaultValuesDisplay( defaultValues );
        return ( descriptor.description() + ' ' + surround( "default: " + defaultValuesDisplay, '(', ')' ) ).trim();
    }

    private String createDefaultValuesDisplay( List<?> defaultValues ) {
        return defaultValues.size() == 1 ? defaultValues.get( 0 ).toString() : defaultValues.toString();
    }

    private void fitRowsToWidth() {
        nonOptionRows.fitToWidth();
        optionRows.fitToWidth();
    }
}
