package joptsimple;

import java.util.Collection;
import java.util.List;

public interface OptionDescriptor {
    Collection<String> options();

    String description();

    List<?> defaultValues();

    boolean acceptsArguments();

    boolean requiresArgument();

    String argumentDescription();

    String argumentTypeIndicator();
}
