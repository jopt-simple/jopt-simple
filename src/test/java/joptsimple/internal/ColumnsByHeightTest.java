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

package joptsimple.internal;

import static joptsimple.internal.Column.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class ColumnsByHeightTest {
    private Column lessThan;
    private Column equalTo;
    private Column greaterThan;

    @Before
    public void setUp() {
        lessThan = new Column( "a", 3 );
        lessThan.addCells( "1 2" );
        equalTo = new Column( "b", 3 );
        equalTo.addCells( "1 2 3 4 5" );
        greaterThan = new Column( "b", 3 );
        greaterThan.addCells( "1 2 3 4 5 6 7 8" );
    }

    @Test
    public void lessThan() {
        assertEquals( 0, BY_HEIGHT.compare( lessThan, lessThan ) );
        assertTrue( BY_HEIGHT.compare( lessThan, equalTo ) < 0 );
        assertTrue( BY_HEIGHT.compare( lessThan, greaterThan ) < 0 );
    }

    @Test
    public void equalTo() {
        assertTrue( BY_HEIGHT.compare( equalTo, lessThan ) > 0 );
        assertEquals( 0, BY_HEIGHT.compare( equalTo, equalTo ) );
        assertTrue( BY_HEIGHT.compare( equalTo, greaterThan ) < 0 );
    }

    @Test
    public void greaterThan() {
        assertTrue( BY_HEIGHT.compare( greaterThan, lessThan ) > 0 );
        assertTrue( BY_HEIGHT.compare( greaterThan, equalTo ) > 0 );
        assertEquals( 0, BY_HEIGHT.compare( greaterThan, greaterThan ) );
    }
}
