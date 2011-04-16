package joptsimple;

import java.util.Collection;
import java.util.Map;

/**
 * <p>Represents objects charged with taking a set of option descriptions and producing some help text from them.</p>
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public interface HelpFormatter {
    String format( Map<String, ? extends OptionDescriptor> options );
}
