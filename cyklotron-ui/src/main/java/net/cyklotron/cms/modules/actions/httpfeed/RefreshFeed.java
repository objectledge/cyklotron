package net.cyklotron.cms.modules.actions.httpfeed;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
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
 * Action for explicit refreshing of http feeds in the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RefreshFeed.java,v 1.2 2005-01-25 03:21:55 pablo Exp $
 */
public class RefreshFeed extends BaseHttpFeedAction
{
    public RefreshFeed(Logger logger, StructureService structureService,
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
        Subject subject = coralSession.getUserSubject();
   
        HttpFeedResource feed = getFeed(coralSession, parameters);
        String content = httpFeedService.getContent(feed.getUrl());
        httpFeedService.refreshFeed(feed, content, subject);
        if(content != null)
        {
            templatingContext.put("result","refreshed_successfully");
        }
        else
        {
            templatingContext.put("result","refresh_failed");
        }
    }
}
