package joptsimple;

import java.util.Collection;


/**
 * <p>Thrown when an option is marked as required, but not specified in the parser's input</p>
 * 
 * @author <a href="https://github.com/TC1">Emils Solmanis</a>
 */
class OptionRequiredException extends OptionException {

	private static final long serialVersionUID = -84650363723336432L;
	
	protected OptionRequiredException(Collection<String> options) {
		super(options);
	}

    @Override
    public String getMessage() {
        return "Option " + multipleOptionMessage() + " is required";
    }
}
