/*
 */
package net.cyklotron.cms.modules.actions.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.aggregation.RecommendationResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;


/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class RecommendationWorkflow extends BaseAggregationAction
{
    
    
    public RecommendationWorkflow(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService,
        AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory, siteService, aggregationService);
        // TODO Auto-generated constructor stub
    }
    
    /* (overriden) */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, 
        TemplatingContext templatingContext, HttpContext httpContext, 
        CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            long recId = parameters.getLong("rec");
            RecommendationResource rec =
                (RecommendationResource)coralSession.getStore().getResource(recId);
            String comment = parameters.get("comment","");
            String event = parameters.get("event");
            Subject subject = coralSession.getUserSubject();
            if("reject".equals(event))
            {
                if(comment.length() == 0)
                {
                    templatingContext.put("result", "comment_missing");
                    mvcContext.setView("aggregation,RecommendationDetails");
                }
                else
                {
                    aggregationService.rejectRecommendation(coralSession,rec, comment, subject);
                }
            }
            else if("resubmit".equals(event))
            {
                if(comment.length() == 0)
                {
                    templatingContext.put("result", "comment_missing");
                    mvcContext.setView("aggregation,RecommendationDetails");
                }
                else
                {
                    aggregationService.resubmitRecommendation(coralSession, rec, comment, subject);
                }
            }
            else if("discard".equals(event))
            {
                aggregationService.discardRecommendation(coralSession, rec, subject);                
            }
            else
            {
                templatingContext.put("result", "unknown_workflow_event");
            }
        }
        catch (Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        } 
    }
    
    /* (overriden) */
    public boolean checkAccessRights(Context context) 
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            Permission impPerm = coralSession.getSecurity().
                getUniquePermission("cms.aggregation.import");
            Permission expPerm = coralSession.getSecurity().
                getUniquePermission("cms.aggregation.import");
            long recId = parameters.getLong("rec");
            Subject subject = coralSession.getUserSubject();
            RecommendationResource rec =
                (RecommendationResource)coralSession.getStore().getResource(recId);
            return subject.hasPermission(rec.getTargetSite(), impPerm) ||
                subject.hasPermission(rec.getSource(), expPerm);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check acccess rights", e);
        }
    }
}
