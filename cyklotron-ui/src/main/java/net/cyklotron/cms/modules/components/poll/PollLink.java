package net.cyklotron.cms.modules.components.poll;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;

/**
 * Poll component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollLink.java,v 1.3 2005-01-26 03:52:26 pablo Exp $
 */

public class PollLink extends SkinableCMSComponent
{
    private PollService pollService;

    
    public PollLink(org.objectledge.context.Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        PollService pollService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.pollService = pollService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
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
            long poolId = componentConfig.getLong("pool_id",-1);
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
