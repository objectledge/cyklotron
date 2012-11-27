package net.cyklotron.cms.modules.views.locations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationDatabaseService;
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
        String district = parameters.get("qdistrict", "");
        String commune = parameters.get("qcommune", "");
        String city = parameters.get("qcity", "");
        String area = parameters.get("qarea", "");
        String street = parameters.get("qstreet", "");
        String postCode = parameters.get("qpostCode", "");
        int limit = parameters.getInt("limit", DEFAULT_LIMIT);

        if("province".equals(requestedField))
        {
            province = query;
        }
        if("district".equals(requestedField))
        {
            district = query;
        }
        if("commune".equals(requestedField))
        {
            commune = query;
        }
        if("city".equals(requestedField))
        {
            city = query;
        }
        if("area".equals(requestedField))
        {
            area = query;
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
            && province.length() + district.length() + commune.length() + city.length()
                + area.length() + street.length() + postCode.length() == 0)
        {
            return locationDatabaseService.getAllTerms("province");
        }
        else if(requestedField.equals("district")
            && province.length() + district.length() + commune.length() + city.length()
                + area.length() + street.length() + postCode.length() == 0)
        {
            return locationDatabaseService.getAllTerms("district");
        }
        else if(requestedField.equals("commune")
            && province.length() + district.length() + commune.length() + city.length()
                + area.length() + street.length() + postCode.length() == 0)
        {
            return locationDatabaseService.getAllTerms("commune");
        }
        else
        {
            List<Location> locations = locationDatabaseService.getLocations(requestedField,
                province, district, commune, city, area, street, postCode);
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
            if("district".equals(requestedField))
            {
                fieldValue = location.getDistrict();
            }
            if("commune".equals(requestedField))
            {
                fieldValue = location.getCommune();
            }
            if("city".equals(requestedField))
            {
                fieldValue = location.getCity();
            }
            if("area".equals(requestedField))
            {
                fieldValue = location.getArea();
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
