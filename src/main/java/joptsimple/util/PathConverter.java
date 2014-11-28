package joptsimple.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

/**
 * Converts cmdline options to {@link Path} objects and checks the
 * status of the underlying file
 */
public class PathConverter implements ValueConverter<Path> {

    private PathProperties[] pathProperties;

    public PathConverter(PathProperties... pathProperties) {
        this.pathProperties = pathProperties;
    }

    @Override
    public Path convert(String s) {
        Path path = Paths.get(s);
        if (pathProperties != null) {
            for (PathProperties pathProperty : pathProperties) {
                if (!pathProperty.accept(path)) {
                    throw new ValueConversionException(message(
                            pathProperty.getMessageKey(),
                            path.toString()));
                }
            }
        }
        return path;
    }

    @Override
    public Class<Path> valueType() {
        return Path.class;
    }

    @Override
    public String valuePattern() {
        return null;
    }

    private String message(String errorKey, String value) {
        ResourceBundle bundle = ResourceBundle.getBundle("joptsimple.ExceptionMessages");
        Object[] arguments = new Object[] { value, valuePattern() };
        String key = PathConverter.class.getName() + "." + errorKey + ".message";
        String template = bundle.getString(key);
        return new MessageFormat(template).format(arguments);
    }
}
