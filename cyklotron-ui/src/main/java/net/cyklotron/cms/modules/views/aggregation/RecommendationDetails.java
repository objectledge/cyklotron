/*
 */
package net.cyklotron.cms.modules.views.aggregation;

import java.util.Arrays;

import net.cyklotron.cms.aggregation.RecommendationResource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class RecommendationDetails 
    extends BaseAggregationScreen
{
    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            long recId = parameters.getLong("rec");
            RecommendationResource rec =
                (RecommendationResource)coralSession.getStore().getResource(recId);
            templatingContext.put("rec", rec);
            templatingContext.put("comments", Arrays.asList(aggregationService.getComments(rec)));
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to retrieve data", e);
        } 
    }
}
