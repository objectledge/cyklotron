package net.cyklotron.cms.modules.actions.httpfeed;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Action for deleting http feeds from the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DeleteFeed.java,v 1.3 2005-01-25 03:21:55 pablo Exp $
 */
public class DeleteFeed extends BaseHttpFeedAction
{
    
    public DeleteFeed(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, HttpFeedService httpFeedService)
    {
        super(logger, structureService, cmsDataFactory, httpFeedService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        HttpFeedResource feed = getFeed(coralSession, parameters);
        try
        {
            coralSession.getStore().deleteResource(feed);
            parameters.remove("feed_id");
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            logger.error("EntityInUseException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.httpfeed.delete");
    }
}
