package net.cyklotron.cms.modules.actions.syndication;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.syndication.EmptyFeedNameException;
import net.cyklotron.cms.syndication.EmptyUrlException;
import net.cyklotron.cms.syndication.FeedAlreadyExistsException;
import net.cyklotron.cms.syndication.FeedCreationException;
import net.cyklotron.cms.syndication.IncomingFeedResource;
import net.cyklotron.cms.syndication.IncomingFeedResourceData;
import net.cyklotron.cms.syndication.SyndicationService;
import net.cyklotron.cms.util.URI.MalformedURIException;

/**
 * Action for updating incoming feeds in the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateIncomingFeed.java,v 1.4 2007-11-18 21:24:34 rafal Exp $
 */
public class UpdateIncomingFeed extends AddIncomingFeed
{
    public UpdateIncomingFeed(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SyndicationService syndicationService)
    {
        super(logger, structureService, cmsDataFactory, syndicationService);
    }
    
    protected IncomingFeedResource getResource(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        return getIncomingFeed(coralSession, parameters);
    }

    protected IncomingFeedResource updateResource(CmsData cmsData, CoralSession coralSession,
        IncomingFeedResourceData feedData, Parameters parameters)
    throws EmptyFeedNameException, FeedCreationException, FeedAlreadyExistsException,
        EmptyUrlException, MalformedURIException, ProcessingException, InvalidResourceNameException  
    {
        IncomingFeedResource feed = getIncomingFeed(coralSession, parameters);
        syndicationService.getIncomingFeedsManager().updateFeed(
            coralSession, feed, feedData.getName(), feedData.getUrl(),
            feedData.getInterval(), feedData.getTemplate());
        if(feedData.getDescription() != null)
        {
            feed.setDescription(feedData.getDescription());
            feed.update();
        }
        return feed;
    }

    protected String successResult()
    {
        return "updated_successfully";
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
        return checkPermission(context, coralSession, "cms.syndication.infeed.modify");
    }
}
