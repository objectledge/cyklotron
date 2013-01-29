package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationsProvider;

/**
 * LocationProvider implementation for Poland using Pocztowe Numery Adresowe (postal area codes)
 * published by Poczta Polska SA.
 * 
 * @author rafal
 */
public class PNALocationsProvider
    implements LocationsProvider
{
    /**
     * The fields defined for location identification for Poland.
     * <ul>
     * <li>postCode: PNA (kod pocztowy)</li>
     * <li>street: nazwa ulicy (placu itp.)</li>
     * <li>city: miejscowość</li>
     * <li>commune: gmina</li>
     * <li>district: powiat</li>
     * <li>province: województwo</li>
     * </ul>
     */
    public static final String[] FIELDS = { "postCode", "street", "city", "commune", "district",
                    "province" };

    private final Logger logger;

    private final FileSystem fileSystem;

    private final PNAProvider pnaProvider;

    private List<Location> cachedLocations = null;

    public PNALocationsProvider(Logger logger, FileSystem fileSystem)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
        pnaProvider = new PNAProvider(fileSystem, logger);
    }

    /**
     * Parse source file. Source file is parsed and on success {@link #cachedContent} variable is
     * updated.
     * 
     * @return true if parsing was successful.
     */
    private void parseSource()
    {
        try
        {
            makeLocations(pnaProvider.parseSource());
        }
        catch(IOException e)
        {
            logger.error("failed to parse source file " + PNAProvider.SOURCE_FILE, e);
        }
    }

    private void makeLocations(List<String[]> content)
    {
        cachedLocations = new ArrayList<Location>(content.size());
        for(String[] row : content)
        {
            Map<String, String> fieldValues = new HashMap<>(4);
            fieldValues.put("province", row[6]);
            fieldValues.put("district", row[5]);
            fieldValues.put("commune", row[4]);
            fieldValues.put("city", stripCityName(row[1]));
            fieldValues.put("street", row[3] != null ? row[3] : "");
            fieldValues.put("postCode", row[0]);
            cachedLocations.add(new Location(FIELDS, fieldValues));
        }
    }

    /**
     * Strip city name from extra information
     */
    private String stripCityName(String city)
    {
        return city != null ? city.replaceFirst("\\s[(].+[)]", "") : null;
    }

    @Override
    public Collection<Location> fromCache()
    {
        if(cachedLocations == null)
        {
            if(fileSystem.exists(PNAProvider.CACHE_DIRECTORY + PNAProvider.CACHE_FILE))
            {
                try
                {
                    makeLocations(pnaProvider.parseCache());
                }
                catch(IOException e)
                {
                    logger.error("failed to parse cache file " + PNAProvider.CACHE_FILE, e);
                }
            }
            else
            {
                fromSource();
            }
        }
        return cachedLocations;
    }

    @Override
    public Collection<Location> fromSource()
    {
        if(pnaProvider.downloadSource())
        {
            parseSource();
        }
        else if(fileSystem.exists(PNAProvider.CACHE_DIRECTORY + PNAProvider.CACHE_FILE))
        {
            fromCache();
        }
        return cachedLocations;
    }

    @Override
    public String[] getFields()
    {
        return FIELDS;
    }

    @Override
    public Set<FieldOptions> getOptions(String field)
    {
        switch(field)
        {
        case "postCode":
            return EnumSet.of(FieldOptions.NOT_ANALYZED);
        case "street":
            return EnumSet.of(FieldOptions.MULTI_TERM_SUBQUERY);
        default:
            return EnumSet.noneOf(FieldOptions.class);
        }
    }
}
