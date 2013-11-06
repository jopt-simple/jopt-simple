/*
 The MIT License

 Copyright (c) 2004-2013 Paul R. Holser, Jr.

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class OptionParserRecognizedOptionsTest extends AbstractOptionParserFixture {
    @Test
    public void basicOptionsRecognized() {
        parser.accepts( "first" ).withRequiredArg().required();
        parser.accepts( "second" ).withOptionalArg();
        parser.accepts( "third" ).forHelp();

        Map<String, OptionSpec<?>> recognizedOptions = parser.recognizedOptions();

        assertEquals(4, recognizedOptions.size());
        assertTrue( recognizedOptions.keySet().contains( "first" ) );
        assertTrue( recognizedOptions.keySet().contains( "second" ) );
        assertTrue( recognizedOptions.keySet().contains( "third" ) );
        assertTrue( recognizedOptions.keySet().contains( "[arguments]" ) );
        assertTrue( recognizedOptions.get( "third" ).isForHelp() );
        assertFalse( recognizedOptions.get("second").isForHelp() );
        assertNotNull( recognizedOptions.get("first").options() );
    }
}
