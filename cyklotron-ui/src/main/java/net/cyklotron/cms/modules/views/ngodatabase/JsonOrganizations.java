package net.cyklotron.cms.modules.views.ngodatabase;

import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
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
import net.cyklotron.cms.ngodatabase.NgoDatabaseService;
import net.cyklotron.cms.ngodatabase.Organization;
import net.cyklotron.cms.preferences.PreferencesService;

import net.sf.json.JSONArray;

/**
 * The screen for serving files.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.6 2006-01-02 11:42:17 rafal Exp $
 */
public class JsonOrganizations
    extends AbstractBuilder
    implements SecurityChecking
{    
    /** The logging service. */
    Logger logger;
    
    /** The NGO database service. */
    NgoDatabaseService ngoDatabaseService;
 
 
    public JsonOrganizations(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        NgoDatabaseService ngoDatabaseService)
    {
        super(context);
        this.logger = logger;
        this.ngoDatabaseService = ngoDatabaseService;
    }
 
    public String build(Template template, String embeddedBuildResults)
    throws BuildException, ProcessingException
    {
        JSONArray jsonArray = new JSONArray();
        try 
        {
            List<Organization> organizations = getRequestedOrganizations(context);
            jsonArray = OrganizationsToJson(organizations);
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
    
    private List<Organization> getRequestedOrganizations(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        String organization = parameters.get("q", "");

        if(organization.equals(""))
        {
            throw new ProcessingException("parameter is missing");
        }
        else
        {
            return this.ngoDatabaseService.getOrganizations(organization);
        }
    }
    
    private JSONArray OrganizationsToJson(List<Organization> organizations)
    {
        JSONArray jsonArray = JSONArray.fromObject(organizations);
        return jsonArray;
    }
}
