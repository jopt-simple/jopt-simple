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
        
        for(String s: recognizedOptions.keySet())
        	System.out.println(s+" : "+recognizedOptions.get(s).toString());
		
        // Parser will actually contain the 3 described fields + 1 dedicated to other arguments
        assertEquals(4, recognizedOptions.size());
        assertTrue(recognizedOptions.keySet().contains("first"));
        assertTrue(recognizedOptions.keySet().contains("second"));
        assertTrue(recognizedOptions.keySet().contains("third"));
        assertTrue(recognizedOptions.keySet().contains("[arguments]"));
        assertTrue(recognizedOptions.get("third").isForHelp());
        assertFalse(recognizedOptions.get("second").isForHelp());
        assertNotNull(recognizedOptions.get("first").options());
	}
}
