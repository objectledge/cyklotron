package net.cyklotron.cms.modules.actions.link;

import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.TemplateAction;

import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.LinkService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FollowLink.java,v 1.2 2005-01-24 10:27:01 pablo Exp $
 */
public class FollowLink
    extends TemplateAction
{
    /** service broker */
    protected ServiceBroker broker;

    /** logging facility */
    protected Logger log;

    /** link service */
    protected LinkService linkService;

    /** resource service */
    protected CoralSession coralSession;


    public FollowLink()
    {
        broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(LinkService.LOGGING_FACILITY);
        linkService = (LinkService)broker.getService(LinkService.SERVICE_NAME);
        coralSession = (CoralSession)broker.getService(CoralSession.SERVICE_NAME);
    }


    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        int lid = parameters.getInt("lid", -1);
        if(lid == -1)
        {
            throw new ProcessingException("Link id not found");
        }
        try
        {
            BaseLinkResource linkResource = BaseLinkResourceImpl.getBaseLinkResource(coralSession,lid);
            linkService.followLink(linkResource);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("ARLException: ",e);
            return;
        }
        templatingContext.put("result","followed_successfully");
    }
}
