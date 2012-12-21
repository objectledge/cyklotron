package net.cyklotron.cms.modules.views.locations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.web.HttpContext;
import org.objectledge.web.json.AbstractJsonView;

import com.fasterxml.jackson.core.JsonGenerationException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationDatabaseService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * The screen for serving files.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.6 2006-01-02 11:42:17 rafal Exp $
 */
public class JsonLocations
    extends AbstractJsonView
{
    private static final int DEFAULT_LIMIT = 25;

    /** The Location service. */
    private LocationDatabaseService locationDatabaseService;

    public JsonLocations(Context context, Logger log, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        LocationDatabaseService locationDatabaseService)
    {
        super(context, log);
        this.locationDatabaseService = locationDatabaseService;
    }

    @Override
    protected void buildResponseHeaders(HttpContext httpContext)
        throws ProcessingException
    {
        httpContext.getResponse().setContentType("application/json;charset=UTF-8");
    }

    @Override
    protected void buildJsonStream()
        throws ProcessingException, JsonGenerationException, IOException
    {
        LocationResponse lotationResponse = getFieldValues(context);
        writeResponseValue(lotationResponse);
    }

    private LocationResponse getFieldValues(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        String requestedField = parameters.get("qfield", "");
        String query = parameters.get("q", "");
        int limit = parameters.getInt("limit", DEFAULT_LIMIT);

        Map<String, String> fieldValues = new HashMap<>();
        for(String param : parameters.getParameterNames())
        {
            if(param.startsWith("q") && !parameters.get(param).isEmpty()
                && !(param.equals("q") || param.equals("qfield")))
            {
                fieldValues.put(param.substring(1), parameters.get(param));
            }
        }
        if(query.length() > 0)
        {
            fieldValues.put(requestedField, query);
        }

        if(fieldValues.size() == 0 && requestedField.length() > 0)
        {
            List<String> terms = locationDatabaseService.getAllTerms(requestedField);
            Collections.sort(terms);
            return new LocationResponse(terms);
        }
        else
        {
            List<Location> locations = locationDatabaseService.getLocations(requestedField,
                fieldValues);
            return getFieldValues(requestedField, locations, limit);
        }
    }

    private LocationResponse getFieldValues(String requestedField, List<Location> locations,
        int limit)
    {
        SortedMap<String, Location> uniqueLocations = new TreeMap<String, Location>();

        for(Location location : locations)
        {
            Location matchnigLocation = new Location(location, uniqueLocations.get(location
                .get(requestedField)));
            uniqueLocations.put(location.get(requestedField), matchnigLocation);
        }
        limit = Math.min(limit, uniqueLocations.size());
        List<String> valueList = new ArrayList<String>(uniqueLocations.keySet());
        if(valueList.size() > 1)
        {
            uniqueLocations = uniqueLocations.headMap(valueList.get(limit - 1));
            valueList = valueList.subList(0, limit);
        }
        return new LocationResponse(valueList, uniqueLocations);
    }

    private class LocationResponse
    {
        private List<String> fieldValues;

        private SortedMap<String, Location> matchingLocations;

        public LocationResponse(List<String> fieldValues,
            SortedMap<String, Location> matchingLocations)
        {
            this.fieldValues = fieldValues;
            this.matchingLocations = matchingLocations;
        }

        public LocationResponse(List<String> fieldValues)
        {
            this.fieldValues = fieldValues;
            this.matchingLocations = new TreeMap<String, Location>();
        }

        public List<String> getFieldValues()
        {
            return fieldValues;
        }

        public SortedMap<String, Location> getMatchingLocations()
        {
            return matchingLocations;
        }
    }

}
