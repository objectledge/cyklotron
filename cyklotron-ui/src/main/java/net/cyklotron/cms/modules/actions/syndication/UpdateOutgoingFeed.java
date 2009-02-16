package net.cyklotron.cms.modules.actions.syndication;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.syndication.EmptyDescriptionException;
import net.cyklotron.cms.syndication.EmptyFeedNameException;
import net.cyklotron.cms.syndication.FeedAlreadyExistsException;
import net.cyklotron.cms.syndication.FeedCreationException;
import net.cyklotron.cms.syndication.OutgoingFeedResource;
import net.cyklotron.cms.syndication.OutgoingFeedResourceData;
import net.cyklotron.cms.syndication.SyndicationService;

/**
 * Action for updating incoming feeds in the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateOutgoingFeed.java,v 1.4 2007-11-18 21:24:34 rafal Exp $
 */
public class UpdateOutgoingFeed extends AddOutgoingFeed
{
    public UpdateOutgoingFeed(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SyndicationService syndicationService,
        CategoryQueryService categoryQueryService)
    {
        super(logger, structureService, cmsDataFactory, syndicationService, categoryQueryService);
    }
    
    protected OutgoingFeedResource getResource(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        return getOutgoingFeed(coralSession, parameters);
    }

    protected OutgoingFeedResource updateResource(CmsData cmsData, CoralSession coralSession,
        OutgoingFeedResourceData feedData, Parameters parameters)
    throws EmptyFeedNameException, FeedCreationException,
        FeedAlreadyExistsException, EmptyDescriptionException, ProcessingException, InvalidResourceNameException
    {
        OutgoingFeedResource feed = getOutgoingFeed(coralSession, parameters);
        syndicationService.getOutgoingFeedsManager().updateFeed(coralSession, feed,
            feedData.getName(), feedData.getDescription(), feedData.getInterval(),
            getCategoryQuery(cmsData, coralSession, feedData.getQueryName()),
            feedData.getTemplate(), feedData.getPublic(),feedData.getSortColumn(), 
            feedData.getSortOrder(), feedData.getPublicationTimeOffset(), feedData.getMaxResNumber());

        updateOptionalFields(feedData, feed);
        
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
