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

import static java.util.Arrays.*;
import java.util.Collection;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
@RunWith( Parameterized.class )
public class ColumnWidthCalculatorTest {
    private static final int WIDTH = 80;

    private final int expectedWidth;
    private final int totalWidth;
    private final int numberOfColumns;

    public ColumnWidthCalculatorTest( int expectedWidth, int totalWidth, int numberOfColumns ) {
        this.expectedWidth = expectedWidth;
        this.totalWidth = totalWidth;
        this.numberOfColumns = numberOfColumns;
    }

    @Parameterized.Parameters
    public static Collection<?> testData() {
        return asList( new Object[][] {
            { 80, WIDTH, 1 },
            { 39, WIDTH, 2 },
            { 26, WIDTH, 3 },
            { 19, WIDTH, 4 },
            { 15, WIDTH, 5 },
            { 12, WIDTH, 6 },
            { 10, WIDTH, 7 },
            { 9, WIDTH, 8 },
            { 8, WIDTH, 9 },
            { 7, WIDTH, 10 },
            { 6, WIDTH, 11 },
            { 5, WIDTH, 12 },
            { 5, WIDTH, 13 },
            { 4, WIDTH, 14 },
            { 4, WIDTH, 15 },
            { 4, WIDTH, 16 },
        } );
    }

    @Test
    public void width() {
        assertEquals( expectedWidth, new ColumnWidthCalculator().calculate( totalWidth, numberOfColumns ) );
    }
}
