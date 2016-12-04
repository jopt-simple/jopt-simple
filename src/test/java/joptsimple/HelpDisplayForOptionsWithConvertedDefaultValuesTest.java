/*
 The MIT License

 Copyright (c) 2004-2016 Paul R. Holser, Jr.

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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Test;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

public class HelpDisplayForOptionsWithConvertedDefaultValuesTest
    extends AbstractOptionParserFixture {

    @Test
    public void respectsRevertedDefaultValues() throws Exception {
        parser.acceptsAll(
            asList( "t", "traceIncrements" ),
            "Turns tracing of increment requests 'on' or 'off'." )
            .withRequiredArg()
            .describedAs( "trace" )
            .withValuesConvertedBy( new SwitchConverter() )
            .defaultsTo( false );
        StringWriter sink = new StringWriter();

        parser.printHelpOn( sink );

        String help = sink.toString();
        assertFalse( help.contains( "false" ) );
    }
}
