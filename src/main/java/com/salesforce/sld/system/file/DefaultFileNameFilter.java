package com.salesforce.sld.system.file;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements an {@linkplain IFileNameFilter} based on a list of regular expressions,
 * {@linkplain DefaultFileNameFilter} supports adding and removing regular expression patterns to
 * the current filter
 *
 * @author sarah.lackey
 */
public class DefaultFileNameFilter
                implements IFileNameFilter
{
    private final List<Pattern> patterns = new ArrayList<>();


    /**
     * Adds a regular expression pattern to the filter for allowed names.
     *
     * @param pattern the pattern to be added to the name filter
     */
    public void addPattern( @Nonnull String pattern )
    {
        try
        {
            patterns.add( Pattern.compile( pattern ) );
        }
        catch ( IllegalArgumentException e )
        {
            throw new IllegalArgumentException( "Invalid pattern provided for addPattern", e );
        }
    }

    /**
     * Removes all regular expression patterns from this filter.
     */
    public void removeAllPatterns()
    {
        patterns.clear();
    }

    @Override
    public boolean test( String name )
    {
        for ( Pattern pattern : patterns )
        {
            Matcher m = pattern.matcher( name );
            if ( m.matches() )
                return true;
        }
        return false;
    }
}
