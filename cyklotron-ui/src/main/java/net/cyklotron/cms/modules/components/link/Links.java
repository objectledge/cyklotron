package net.cyklotron.cms.modules.components.link;


import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;


/**
 * Link component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: Links.java,v 1.4 2005-12-14 14:09:31 pablo Exp $
 */

public class Links
    extends SkinableCMSComponent
{
    private LinkService linkService;
    
    public Links(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        LinkService linkService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.linkService = linkService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        if(getSite(context) == null)
        {
            componentError(context, "No site selected");
            return;
        }
        try
        {
            Parameters componentConfig = getConfiguration();
            LinkRootResource linksResource = linkService.getLinkRoot(coralSession, getSite(context));
            List links = linkService.getLinks(coralSession, linksResource, componentConfig);
            if(links == null)
            {
                links = new ArrayList();
            }
            templatingContext.put("links",links);
            templatingContext.put("header", componentConfig.get("header", ""));
        }
        catch(LinkException e)
        {
            componentError(context, "Link Exception", e);
        }
    }
}
