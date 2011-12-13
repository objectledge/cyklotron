package net.cyklotron.cms.modules.views.ngodatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.ngodatabase.Location;
import net.cyklotron.cms.ngodatabase.LocationDatabaseService;
import net.cyklotron.cms.preferences.PreferencesService;

import org.codehaus.jackson.JsonGenerationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.web.json.AbstractJsonView;

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
        String province = parameters.get("qprovince", "");
        String city = parameters.get("qcity", "");
        String street = parameters.get("qstreet", "");
        String postCode = parameters.get("qpostCode", "");
        int limit = parameters.getInt("limit", DEFAULT_LIMIT);

        if("province".equals(requestedField))
        {
            province = query;
        }
        if("city".equals(requestedField))
        {
            city = query;
        }
        if("street".equals(requestedField))
        {
            street = query;
        }
        if("postCode".equals(requestedField))
        {
            postCode = query;
        }

        if(requestedField.equals("province")
            && province.length() + city.length() + street.length() + postCode.length() == 0)
        {
            return locationDatabaseService.getAllTerms("province");
        }
        else
        {
            List<Location> locations = locationDatabaseService.getLocations(requestedField, province,
                city, street, postCode);
            return getFieldValues(requestedField, locations, limit);
        }        
    }

    private List<String> getFieldValues(String requestedField, List<Location> locations, int limit)
    {
        Set<String> valueSet = new HashSet<String>(locations.size());
        for(Location location : locations)
        {
            String fieldValue = null;
            if("province".equals(requestedField))
            {
                fieldValue = location.getProvince();
            }
            if("city".equals(requestedField))
            {
                fieldValue = location.getCity();
            }
            if("street".equals(requestedField))
            {
                fieldValue = location.getStreet();
            }
            if("postCode".equals(requestedField))
            {
                fieldValue = location.getPostCode();
            }
            valueSet.add(fieldValue);
        }
        List<String> valueList = new ArrayList<String>(valueSet);
        Collections.sort(valueList);
        return valueList.subList(0, Math.min(limit, valueList.size()));
    }
}
