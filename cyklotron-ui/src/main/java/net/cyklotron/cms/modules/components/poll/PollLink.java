package net.cyklotron.cms.modules.components.poll;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Poll component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollLink.java,v 1.1 2005-01-24 04:35:23 pablo Exp $
 */

public class PollLink extends SkinableCMSComponent
{
    private PollService pollService;

    public PollLink()
    {
        pollService = (PollService)broker.getService(PollService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(PollService.LOGGING_FACILITY);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) throws ProcessingException
    {
    	SiteResource site = getSite(context); 
        if(site == null)
        {
            componentError(context, "No site selected");
            return;
        }

        try
        {
            Parameters componentConfig = getConfiguration();
            long poolId = componentConfig.get("pool_id").asLong(-1);
            templatingContext.put("pool_id",new Long(poolId));
            String path = componentConfig.get("pollNodePath","");
    	    if(path == null || path.length()==0)
    	    {
    	    	componentError(context, "Poll section not configured");
    			return;
    	    }
            CmsData cmsData = cmsDataFactory.getCmsData(context);
	    	Resource parent = cmsData.getHomePage().getParent();
    	   	Resource[] nodes = coralSession.getStore().getResourceByPath(parent, path);
    	   	if(nodes.length != 1)
    	   	{
    	   		componentError(context, "Cannot find resource with path '"+parent.getPath()+path+"'");
    			return;
    	   	}
           	templatingContext.put("poll_node_id",new Long(nodes[0].getId()));
        }
        catch(Exception e)
        {
			componentError(context, "Exception occured: "+e);
			return;			
        }
    }
}
