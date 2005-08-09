package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationNode;
import net.cyklotron.cms.aggregation.AggregationNodeImpl;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FixToLedge21.java,v 1.1.2.2 2005-08-09 04:30:06 rafal Exp $
 */
public class FixToLedge21
    extends BaseCMSAction
{
	SiteService siteService;
	
	SkinService skinService;
	
	IntegrationService integrationService;
	
    public FixToLedge21(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
			SiteService siteService, SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory);
		this.siteService = siteService;
		this.skinService = skinService;
		this.integrationService = integrationService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource[] sites = siteService.getSites(coralSession);
    		for(SiteResource site: sites)
    		{
    		    Resource[] res = coralSession.getStore().getResource(site, "applications");
    	        if(res.length > 0)
    	        {
    	            Resource applicationRoot = res[0];
    	            res = coralSession.getStore().getResource(res[0], "aggregation");
                    if(res.length > 0)
                    {
                        Resource oldRoot = res[0];
                        if(!(oldRoot instanceof AggregationNode))
                        {
                            AggregationNode newRoot = AggregationNodeImpl.
                                createAggregationNode(coralSession, "aggregation", applicationRoot);
                            newRoot.setReplyTo("");
                            newRoot.update();
                            Resource[] children = coralSession.getStore().getResource(oldRoot);
                            for(Resource child: children)
                            {
                                coralSession.getStore().setParent(child, newRoot);
                            }
                            coralSession.getStore().deleteResource(oldRoot);
                        }
                    }
    	        }
    		}
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to fix aggregation node", e);
        }
	}
}
