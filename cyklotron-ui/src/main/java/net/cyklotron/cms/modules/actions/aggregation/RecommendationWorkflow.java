/*
 */
package net.cyklotron.cms.modules.actions.aggregation;

import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.aggregation.RecommendationResource;


/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class RecommendationWorkflow extends BaseAggregationAction
{
    /* (overriden) */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
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
                    data.setView("aggregation,RecommendationDetails");
                }
                else
                {
                    aggregationService.rejectRecommendation(rec, comment, subject);
                }
            }
            else if("resubmit".equals(event))
            {
                if(comment.length() == 0)
                {
                    templatingContext.put("result", "comment_missing");
                    data.setView("aggregation,RecommendationDetails");
                }
                else
                {
                    aggregationService.resubmitRecommendation(rec, comment, subject);
                }
            }
            else if("discard".equals(event))
            {
                aggregationService.discardRecommendation(rec, subject);                
            }
            else
            {
                templatingContext.put("result", "unknown_workflow_event");
            }
        }
        catch (Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        } 
    }
    
    /* (overriden) */
    public boolean checkAccess(RunData data) 
        throws ProcessingException
    {
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
