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

import java.util.List;

import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class OptionSetSpecsWithNonOptionsTest extends AbstractOptionParserFixture {
    @Test
    public void intermixOptionsAndNonOptions() {
        parser.accepts( "a" );
        parser.accepts( "b" ).withRequiredArg();
        parser.accepts( "c" ).withOptionalArg();

        OptionSet options =
            parser.parse("-a", "foo", "bar", "-b", "baz", "xx", "-c", "quux", "d" );

        List<OptionSpec<?>> specs = options.specsWithNonOptions();
        assertEquals(
            asList(
                "a",
                NonOptionArgumentSpec.NAME,
                NonOptionArgumentSpec.NAME,
                "b",
                NonOptionArgumentSpec.NAME,
                "c",
                NonOptionArgumentSpec.NAME
            ),
            specs.stream()
                .flatMap( s -> s.options().stream() )
                .collect( toList() ));
    }
}
