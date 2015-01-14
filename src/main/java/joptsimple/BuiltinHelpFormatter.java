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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import joptsimple.internal.Rows;
import joptsimple.internal.Strings;

import static joptsimple.ParserRules.*;
import static joptsimple.internal.Classes.*;
import static joptsimple.internal.Strings.*;

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

    /**
     * Makes a formatter with a pre-configured overall row width and column separator width.
     */
    BuiltinHelpFormatter() {
        this( 80, 2 );
    }

    /**
     * Makes a formatter with a given overall row width and column separator width.
     *
     * @param desiredOverallWidth how many characters wide to make the overall help display
     * @param desiredColumnSeparatorWidth how many characters wide to make the separation between option column and
     * description column
     */
    public BuiltinHelpFormatter( int desiredOverallWidth, int desiredColumnSeparatorWidth ) {
        nonOptionRows = new Rows( desiredOverallWidth * 2, 0 );
        optionRows = new Rows( desiredOverallWidth, desiredColumnSeparatorWidth );
    }

    public String format( Map<String, ? extends OptionDescriptor> options ) {
        Comparator<OptionDescriptor> comparator =
            new Comparator<OptionDescriptor>() {
                public int compare( OptionDescriptor first, OptionDescriptor second ) {
                    return first.options().iterator().next().compareTo( second.options().iterator().next() );
                }
            };

        Set<OptionDescriptor> sorted = new TreeSet<OptionDescriptor>( comparator );
        sorted.addAll( options.values() );

        addRows( sorted );

        return formattedHelpOutput();
    }

    protected void addOptionRow( String single ) {
        addOptionRow( single, "" );
    }

    protected void addOptionRow( String left, String right ) {
        optionRows.add( left, right );
    }

    protected void addNonOptionRow( String single ) {
        addNonOptionRow(single, "");
    }

    protected void addNonOptionRow( String left, String right ) {
        nonOptionRows.add( left, right );
    }

    protected String nonOptionOutput() {
        return nonOptionRows.render();
    }

    protected String optionOutput() {
        return optionRows.render();
    }

    protected void fitRowsToWidth() {
        nonOptionRows.fitToWidth();
        optionRows.fitToWidth();
    }

    protected String formattedHelpOutput() {
        StringBuilder formatted = new StringBuilder();
        String nonOptionDisplay = nonOptionOutput();
        if ( !Strings.isNullOrEmpty( nonOptionDisplay ) )
            formatted.append( nonOptionDisplay ).append( LINE_SEPARATOR );
        formatted.append( optionOutput() );

        return formatted.toString();
    }

    protected void addRows( Collection<? extends OptionDescriptor> options ) {
        addNonOptionsDescription( options );

        if ( options.isEmpty() )
            addOptionRow( "No options specified" );
        else {
            addHeaders( options );
            addOptions( options );
        }

        fitRowsToWidth();
    }

    protected void addNonOptionsDescription( Collection<? extends OptionDescriptor> options ) {
        OptionDescriptor nonOptions = findAndRemoveNonOptionsSpec( options );
        if ( shouldShowNonOptionArgumentDisplay( nonOptions ) ) {
            addNonOptionRow( "Non-option arguments:" );
            addNonOptionRow( createNonOptionArgumentsDisplay( nonOptions ) );
        }
    }

    protected boolean shouldShowNonOptionArgumentDisplay( OptionDescriptor nonOptionDescriptor ) {
        return !Strings.isNullOrEmpty( nonOptionDescriptor.description() )
            || !Strings.isNullOrEmpty( nonOptionDescriptor.argumentTypeIndicator() )
            || !Strings.isNullOrEmpty( nonOptionDescriptor.argumentDescription() );
    }

    protected String createNonOptionArgumentsDisplay( OptionDescriptor nonOptionDescriptor ) {
        StringBuilder buffer = new StringBuilder();
        maybeAppendOptionInfo( buffer, nonOptionDescriptor );
        maybeAppendNonOptionsDescription( buffer, nonOptionDescriptor );

        return buffer.toString();
    }

    protected void maybeAppendNonOptionsDescription( StringBuilder buffer, OptionDescriptor nonOptions ) {
        buffer.append( buffer.length() > 0 && !Strings.isNullOrEmpty( nonOptions.description() ) ? " -- " : "" )
            .append( nonOptions.description() );
    }

    protected OptionDescriptor findAndRemoveNonOptionsSpec( Collection<? extends OptionDescriptor> options ) {
        for ( Iterator<? extends OptionDescriptor> it = options.iterator(); it.hasNext(); ) {
            OptionDescriptor next = it.next();
            if ( next.representsNonOptions() ) {
                it.remove();
                return next;
            }
        }

        throw new AssertionError( "no non-options argument spec" );
    }

    protected void addHeaders( Collection<? extends OptionDescriptor> options ) {
        if ( hasRequiredOption( options ) ) {
            addOptionRow( "Option (* = required)", "Description" );
            addOptionRow( "---------------------", "-----------" );
        } else {
            addOptionRow( "Option", "Description" );
            addOptionRow( "------", "-----------" );
        }
    }

    protected final boolean hasRequiredOption( Collection<? extends OptionDescriptor> options ) {
        for ( OptionDescriptor each : options ) {
            if ( each.isRequired() )
                return true;
        }

        return false;
    }

    protected void addOptions( Collection<? extends OptionDescriptor> options ) {
        for ( OptionDescriptor each : options ) {
            if ( !each.representsNonOptions() )
                addOptionRow( createOptionDisplay( each ), createDescriptionDisplay( each ) );
        }
    }

    protected String createOptionDisplay( OptionDescriptor descriptor ) {
        StringBuilder buffer = new StringBuilder( descriptor.isRequired() ? "* " : "" );

        for ( Iterator<String> i = descriptor.options().iterator(); i.hasNext(); ) {
            String option = i.next();
            buffer.append( optionLeader( option ) );
            buffer.append( option );

            if ( i.hasNext() )
                buffer.append( ", " );
        }

        maybeAppendOptionInfo( buffer, descriptor );

        return buffer.toString();
    }

    protected String optionLeader( String option ) {
        return option.length() > 1 ? DOUBLE_HYPHEN : HYPHEN;
    }

    protected void maybeAppendOptionInfo( StringBuilder buffer, OptionDescriptor descriptor ) {
        String indicator = extractTypeIndicator( descriptor );
        String description = descriptor.argumentDescription();
        if ( indicator != null || !isNullOrEmpty( description ) )
            appendOptionHelp( buffer, indicator, description, descriptor.requiresArgument() );
    }

    protected String extractTypeIndicator( OptionDescriptor descriptor ) {
        String indicator = descriptor.argumentTypeIndicator();

        if ( !isNullOrEmpty( indicator ) && !String.class.getName().equals( indicator ) )
            return shortNameOf( indicator );

        return null;
    }

    protected void appendOptionHelp( StringBuilder buffer, String typeIndicator, String description,
                                     boolean required ) {
        if ( required )
            appendTypeIndicator( buffer, typeIndicator, description, '<', '>' );
        else
            appendTypeIndicator( buffer, typeIndicator, description, '[', ']' );
    }

    protected void appendTypeIndicator( StringBuilder buffer, String typeIndicator, String description,
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

    protected String createDescriptionDisplay( OptionDescriptor descriptor ) {
        List<?> defaultValues = descriptor.defaultValues();
        if ( defaultValues.isEmpty() )
            return descriptor.description();

        String defaultValuesDisplay = createDefaultValuesDisplay( defaultValues );
        return ( descriptor.description() + ' ' + surround( "default: " + defaultValuesDisplay, '(', ')' ) ).trim();
    }

    protected String createDefaultValuesDisplay( List<?> defaultValues ) {
        return defaultValues.size() == 1 ? defaultValues.get( 0 ).toString() : defaultValues.toString();
    }
}
