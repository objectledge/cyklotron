package net.cyklotron.cms.modules.actions.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Send the resource recommendation to the chosen sites.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RecommendResource.java,v 1.3 2005-03-08 10:50:46 pablo Exp $
 */
public class RecommendResource
    extends BaseAggregationAction
{
    
    public RecommendResource(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService,
        AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory, siteService, aggregationService);
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        long resourceId = parameters.getLong("res_id", -1);
        String comment = parameters.get("comment","");
        String[] keys = parameters.getParameterNames();
        boolean done = false;
        try
        {
            Resource resource = coralSession.getStore().getResource(resourceId);
            for(int i = 0; i < keys.length; i++)
            {
                if(keys[i].startsWith("recommend_"))
                {
                    long id = Long.parseLong(keys[i].substring(10));
                    SiteResource site = SiteResourceImpl.getSiteResource(coralSession, id);
                    aggregationService.submitRecommendation(coralSession, resource, site, comment, subject);
                    done = true;
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("AggregationException: ",e);
            return;
        }
        if(done)
        {
            templatingContext.put("result","recommended_successfully");
        }
        else
        {
            templatingContext.put("result","no_site_chosen");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            SiteResource node = getSite(context);
            Permission permission = coralSession.getSecurity()
                .getUniquePermission("cms.aggregation.export");
            return coralSession.getUserSubject().hasPermission(node, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }
}
