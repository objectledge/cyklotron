/*
 */
package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.aggregation.AggregationListener;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 */
public class InstallAggregation extends BaseCMSAction
{
	protected Logger log;
	
	public InstallAggregation()
	{
		log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("fixes");
	}
	
	
    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException, NotFoundException
    {
        Resource[] sites = coralSession.getStore().
            getResourceByPath("/cms/sites/*");
        // make sure applications nodes exist
        AggregationListener helper = new AggregationListener();
        for (int i = 0; i < sites.length; i++)
        {
        	SiteResource site = (SiteResource)sites[i];
        	if(!site.getTemplate())
        	{
        		try
        		{
        			helper.createSite(null, sites[i].getName());
        		}
        		catch(Exception e)
        		{
        			log.error("Couldn't install aggregation for site '"+sites[i].getName()+"'",e);
        		}
        	}
        }        
    }

    /* 
     * (overriden)
     */
    public boolean checkAccess(RunData data) throws ProcessingException
    {
        return checkAdministrator(context, coralSession);
    }
}
