package net.cyklotron.cms.modules.components.httpfeed;

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

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.httpfeed.HttpFeedException;
import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;

/**
 * FeedView component displays http feed contents.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FeedView.java,v 1.2 2005-01-25 11:24:22 pablo Exp $
 */

public class FeedView extends SkinableCMSComponent
{
    /** The httpfeed service. */
    private HttpFeedService httpFeedService;

    public FeedView(org.objectledge.context.Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        HttpFeedService httpFeedService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.httpFeedService = httpFeedService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(getSite(context) == null)
        {
            componentError(context, "No site selected");
            return;
        }

        try
        {
            Parameters componentConfig = getConfiguration();
            Resource parent = httpFeedService.getFeedsParent(coralSession, getSite(context));

            String name = componentConfig.get("feedName",null);
            if(name == null)
            {
                componentError(context, "Http feed name is not configured");
                return;
            }

            Resource[] res = coralSession.getStore().getResource(parent, name);
            if(res.length == 1)
            {
                HttpFeedResource feed = (HttpFeedResource)res[0];
                templatingContext.put("feed",feed);
            }
            else if(res.length == 0)
            {
                componentError(context, "Cannot find a http feed with name='"+name+"'");
                return;
            }
            else
            {
                componentError(context, "Multiple http feeds with name='"+name+"'");
                return;
            }
        }
        catch(HttpFeedException e)
        {
            componentError(context, "Cannot get http feed root", e);
            return;
        }
    }
}
