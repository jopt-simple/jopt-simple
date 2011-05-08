package joptsimple.examples;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import org.junit.Test;

public class RequiredOptionsTest {
    @Test( expected = OptionException.class )
    public void shouldAllowSpecificationOfRequiredOptions() throws Exception {
        OptionParser parser = new OptionParser() {
            {
                accepts( "userid" ).withRequiredArg().required();
                accepts( "password" ).withRequiredArg().required();
            }
        };

        parser.parse( "--userid", "bob" );
    }
}
