package net.cyklotron.cms.modules.components.link;


import java.util.List;

import net.labeo.services.logging.LoggingService;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;


/**
 * Link component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: Links.java,v 1.1 2005-01-24 04:35:21 pablo Exp $
 */

public class Links
    extends SkinableCMSComponent
{
    private LinkService linkService;

    public Links()
    {
        linkService = (LinkService)broker.getService(LinkService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(LinkService.LOGGING_FACILITY);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) throws ProcessingException
    {
        if(getSite(context) == null)
        {
            componentError(context, "No site selected");
            return;
        }
        try
        {
            Parameters componentConfig = getConfiguration();
            LinkRootResource linksResource = linkService.getLinkRoot(getSite(context));
            List links = linkService.getLinks(linksResource, componentConfig);
            templatingContext.put("links",links);
        }
        catch(LinkException e)
        {
            componentError(context, "Link Exception", e);
        }
    }
}
