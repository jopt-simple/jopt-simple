/*
 The MIT License

 Copyright (c) 2004-2021 Paul R. Holser, Jr.

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

package tests.joptsimple;

import joptsimple.OptionParser;
import joptsimple.OptionSpecBuilder;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.infinitest.toolkit.CollectionMatchers.hasSameContentsAs;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class NoArgumentOptionSpecTest extends AbstractOptionSpecFixture {
    @Override
    protected OptionSpecBuilder createEqualOptionSpecInstance() {
        OptionParser parser = new OptionParser();
        return parser.accepts( "a" );
    }

    @Override
    protected OptionSpecBuilder createNotEqualOptionSpecInstance() {
        OptionParser parser = new OptionParser();
        return parser.accepts( "b" );
    }

    @Test
    public void optionsContents() {
        assertThat( createNotEqualOptionSpecInstance().options(), hasSameContentsAs( singletonList( "b" ) ) );
    }
}
