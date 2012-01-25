package net.cyklotron.cms.modules.components.syndication;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.syndication.IncomingFeedResource;
import net.cyklotron.cms.syndication.SyndicationService;

/**
 * IncomingFeedView component displays transformed incoming feed contents.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IncomingFeedView.java,v 1.2 2007-11-18 21:25:30 rafal Exp $
 */

public class IncomingFeedView extends SkinableCMSComponent
{
    /** The syndication service. */
    private SyndicationService syndicationService;

    public IncomingFeedView(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory,
        SkinService skinService, MVCFinder mvcFinder, SyndicationService syndicationService)
    {
        super(context, logger, templating, cmsDataFactory,skinService, mvcFinder);
        this.syndicationService = syndicationService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
    throws org.objectledge.pipeline.ProcessingException
    {
        SiteResource site = getComponentDataSite();
        
        if(site == null)
        {
            componentError(context, "No site selected");
            return;
        }

        Parameters componentConfig = getConfiguration();
        Resource parent = null;
        try
        {
            parent = syndicationService.getIncomingFeedsManager().getFeedsParent(
                coralSession, site);
        }
        catch(Exception e)
        {
            componentError(context, "No feeds parent in site");
            return;
        }

        String name = componentConfig.get("feedName", null);
        if(name == null)
        {
            componentError(context, "Feed name is not configured");
            return;
        }

        Resource[] res = coralSession.getStore().getResource(parent, name);
        if(res.length == 1)
        {
            IncomingFeedResource feed = (IncomingFeedResource)res[0];
            templatingContext.put("feed",feed);
        }
        else if(res.length == 0)
        {
            componentError(context, "Cannot find a feed with name='"+name+"'");
            return;
        }
        else
        {
            componentError(context, "Multiple feeds with name='"+name+"'");
            return;
        }
    }
}
