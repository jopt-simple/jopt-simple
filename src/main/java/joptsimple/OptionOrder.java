package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;

/**
 * {@code OptionOrder} orders option parser specs into maps of option-spec pairs. Each enum is a strategy for ordering.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
public enum OptionOrder {
    /**
     * Orders option parser specs alphabetically by first option. This is subtly different from abbreviation order used
     * in the parser. An example:
     * <table>
     * <tr>
     * <th>First option order</th>
     * <th>Abbreviation order</th>
     * </tr>
     * <tr>
     * <td>-a, --thermidor</td>
     * <td>-b, --cabinet</td>
     * </tr>
     * <tr>
     * <td>-b, --cabinet</td>
     * <td>-a, --thermidor</td>
     * </tr>
     * </table>
     */
    FIRST_OPTION_ORDER {
        @Override
        public List<OptionSpec<?>> of( OptionParser optionParser ) {
            Comparator<OptionSpec<?>> byFirstOption = new Comparator<OptionSpec<?>>() {
                public int compare( OptionSpec<?> first, OptionSpec<?> second ) {
                    return first.options().iterator().next().compareTo( second.options().iterator().next() );
                }
            };

            List<OptionSpec<?>> sorted = new ArrayList<OptionSpec<?>>( optionParser.abbreviationOrder() );
            sort( sorted, byFirstOption );
            return sorted;
        }
    },
    /**
     * Orders option parser specs by their training. Use this to manually control the order of specs displayed in, for
     * example, help by creating specs in the same order as they should appear when using this strategy.
     */
    TRAINING_ORDER {
        @Override
        public List<? extends OptionSpec<?>> of( OptionParser optionParser ) {
            return optionParser.trainingOrder();
        }
    };

    /**
     * Creates new list of unique specs for the option parser using this ordering strategy.
     *
     * @param optionParser the option parser, never missing
     * @return the sorted specs, never missing
     */
    public abstract List<? extends OptionSpec<?>> of( OptionParser optionParser );

    /** @todo Does not belong here */
    public static Map<String, ? extends OptionSpec<?>> asMap( Collection<? extends OptionSpec<?>> specs ) {
        final Map<String, OptionSpec<?>> map = new LinkedHashMap<String, OptionSpec<?>>();
        for ( OptionSpec<?> spec : specs )
            for ( String option : spec.options() )
                map.put( option, spec );
        return map;
    }
}
