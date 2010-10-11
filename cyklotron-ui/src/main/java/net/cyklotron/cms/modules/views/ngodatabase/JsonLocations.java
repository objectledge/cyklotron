package net.cyklotron.cms.modules.views.ngodatabase;

import java.util.HashSet;

import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Template;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.EnclosingView;
import org.objectledge.web.mvc.security.SecurityChecking;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.ngodatabase.Location;
import net.cyklotron.cms.ngodatabase.LocationDatabaseService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * The screen for serving files.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.6 2006-01-02 11:42:17 rafal Exp $
 */
public class JsonLocations
    extends AbstractBuilder
    implements SecurityChecking
{
    /** location types **/
    public static final String LOCATION_TYPE_CITY = "city";

    public static final String LOCATION_TYPE_POSTCODE = "postCode";

    public static final String LOCATION_TYPE_PROVINCE = "province";

    /** The logging service. */
    Logger logger;

    /** The Location service. */
    LocationDatabaseService locationDatabaseService;

    public JsonLocations(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        LocationDatabaseService locationDatabaseService)
    {
        super(context);
        this.logger = logger;
        this.locationDatabaseService = locationDatabaseService;
    }

    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        JSONArray jsonArray = new JSONArray();
        try
        {
            Set locations = getRequestedLocations(context);
            jsonArray = LocationsToJson(locations);
        }
        catch(Exception e)
        {
            logger.error("exception occured", e);
        }
        return jsonArray.toString();
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }

    private Set getRequestedLocations(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        String location = parameters.get("q", "");
        String locationType = parameters.get("qtype", "");
        String city = parameters.get("qcity", "");
        String postCode = parameters.get("qpostCode", "");
        String province = parameters.get("qprovince", "");

        if(city.equals("") && postCode.equals("") && province.equals(""))
        {
            if(LOCATION_TYPE_POSTCODE.equals(locationType))
            {
                return locationDatabaseService.getPostCodes(location);
            }
            else if(LOCATION_TYPE_CITY.equals(locationType))
            {
                return locationDatabaseService.getCities(location);
            }
            else if(LOCATION_TYPE_PROVINCE.equals(locationType))
            {
                return locationDatabaseService.getProvinces(location);
            }
            else
            {
                return new HashSet<String>();
            }
        }
        else
        {
            Set<Location> locations = locationDatabaseService.getLocationByQuery(postCode, city,
                province);
            Set<String> results = new HashSet<String>();
            
            if(LOCATION_TYPE_POSTCODE.equals(locationType))
            {
                for(Location l : locations)
                {
                    if(l.getPostCode().contains(location) && !results.contains(l.getPostCode()))
                    {
                        results.add(l.getPostCode());
                    }
                }
            }
            else if(LOCATION_TYPE_CITY.equals(locationType))
            {
                for(Location l : locations)
                {
                    if(l.getCity().contains(location) && !results.contains(l.getCity()))
                    {
                        results.add(l.getCity());
                    }
                }
            }
            else if(LOCATION_TYPE_PROVINCE.equals(locationType))
            {
                for(Location l : locations)
                {
                    if(l.getProvince().contains(location) && !results.contains(l.getProvince()))
                    {
                        results.add(l.getProvince());
                    }
                }
            }
            return results;
        }
    }

    private JSONArray LocationsToJson(Set locations)
    {
        JSONArray jsonArray = JSONArray.fromObject(locations);
        return jsonArray;
    }

}
