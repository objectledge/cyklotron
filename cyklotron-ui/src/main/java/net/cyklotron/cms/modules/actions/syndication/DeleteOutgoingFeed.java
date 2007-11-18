package net.cyklotron.cms.modules.actions.syndication;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.syndication.OutgoingFeedResource;
import net.cyklotron.cms.syndication.OutgoingFeedUtil;
import net.cyklotron.cms.syndication.SyndicationService;

/**
 * Action for deleting outgoing feeds from the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DeleteOutgoingFeed.java,v 1.3 2007-11-18 21:24:34 rafal Exp $
 */
public class DeleteOutgoingFeed extends BaseSyndicationAction
{
    public DeleteOutgoingFeed(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SyndicationService syndicationService)
    {
        super(logger, structureService, cmsDataFactory, syndicationService);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
       OutgoingFeedResource feed = getOutgoingFeed(coralSession, parameters);
        try
        {
            syndicationService.getOutgoingFeedsManager().deleteFeed(coralSession, feed);
            parameters.remove(OutgoingFeedUtil.FEED_ID_PARAM);
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result",e.getClass().getSimpleName());
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccessRights(Context context)
    throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("syndication"))
        {
            logger.debug("Application 'syndication' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.syndication.outfeed.delete");
    }
}
