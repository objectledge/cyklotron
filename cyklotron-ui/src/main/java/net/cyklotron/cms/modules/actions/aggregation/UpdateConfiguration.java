package net.cyklotron.cms.modules.actions.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationNode;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Updates forum application configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateConfiguration.java,v 1.2 2007-11-18 21:26:02 rafal Exp $
 */
public class UpdateConfiguration
    extends BaseAggregationAction
{
    
    public UpdateConfiguration(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService, AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory, siteService, aggregationService);
        
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
    throws ProcessingException
    {
        SiteResource site = getSite(context);
        try 
        {
            String replyTo = parameters.get("reply_to","");
            AggregationNode aggregationNode = aggregationService.getAggregationRoot(coralSession, site, true);
            aggregationNode.setReplyTo(replyTo);
            aggregationNode.update();
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
        templatingContext.put("result","updated_successfully");
    }
}
