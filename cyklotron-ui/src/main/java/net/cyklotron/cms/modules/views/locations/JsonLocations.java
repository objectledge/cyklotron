package net.cyklotron.cms.modules.views.locations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.web.HttpContext;
import org.objectledge.web.json.AbstractJsonView;

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
        List<String> fieldValues = getFieldValues(context);
        writeResponseValue(fieldValues);
    }

    private List<String> getFieldValues(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        String requestedField = parameters.get("qfield", "");
        String query = parameters.get("q", "");
        int limit = parameters.getInt("limit", DEFAULT_LIMIT);

        Map<String, String> fieldValues = new HashMap<>();
        for(String param : parameters.getParameterNames())
        {
            if(param.startsWith("q") && !(param.equals("q") || param.equals("qfield")))
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
            return locationDatabaseService.getAllTerms(requestedField);
        }
        else
        {
            List<Location> locations = locationDatabaseService.getLocations(requestedField,
                fieldValues);
            return getFieldValues(requestedField, locations, limit);
        }
    }

    private List<String> getFieldValues(String requestedField, List<Location> locations, int limit)
    {
        Set<String> valueSet = new HashSet<String>(locations.size());
        for(Location location : locations)
        {
            valueSet.add(location.get(requestedField));
        }
        List<String> valueList = new ArrayList<String>(valueSet);
        Collections.sort(valueList);
        return valueList.subList(0, Math.min(limit, valueList.size()));
    }
}
