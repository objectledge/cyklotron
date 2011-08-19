package net.cyklotron.cms.modules.views.ngodatabase;

import java.io.IOException;
import java.util.List;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.ngodatabase.NgoDatabaseService;
import net.cyklotron.cms.ngodatabase.Organization;
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
public class JsonOrganizations
    extends AbstractJsonView
{   
    /** The NGO database service. */
    NgoDatabaseService ngoDatabaseService;
 
    public JsonOrganizations(Context context, Logger log, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        NgoDatabaseService ngoDatabaseService)
    {
        super(context, log);
        this.ngoDatabaseService = ngoDatabaseService;
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
            return this.ngoDatabaseService.getOrganizations(organization);
        }
    }
}
