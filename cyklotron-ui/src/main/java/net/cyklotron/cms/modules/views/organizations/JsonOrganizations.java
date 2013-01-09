package net.cyklotron.cms.modules.views.organizations;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.web.json.AbstractJsonView;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.organizations.Organization;
import net.cyklotron.cms.organizations.OrganizationRegistryService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * The screen for serving files.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.6 2006-01-02 11:42:17 rafal Exp $
 */
public class JsonOrganizations
    extends AbstractJsonView
{   
    /** The NGO database service. */
    OrganizationRegistryService organizationsRegistry;
 
    public JsonOrganizations(Context context, Logger log, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        OrganizationRegistryService ngoDatabaseService)
    {
        super(context, log);
        this.organizationsRegistry = ngoDatabaseService;
    }

    @Override
    protected void buildJsonStream()
        throws ProcessingException, JsonGenerationException, IOException
    {
        List<Organization> organizations = getRequestedOrganizations(context);
        writeResponseValue(organizations);
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
            return this.organizationsRegistry.getOrganizations(organization);
        }
    }
}
