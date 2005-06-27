package net.cyklotron.cms.modules.actions.syndication;

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

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 * Action for adding incoming feeds to the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddIncomingFeed.java,v 1.2 2005-06-27 05:30:25 zwierzem Exp $
 */
public class AddIncomingFeed extends BaseSyndicationAction
{
    public AddIncomingFeed(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SyndicationService syndicationService)
    {
        super(logger, structureService, cmsDataFactory, syndicationService);
    }
    
    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        
        IncomingFeedResource feed = getResource(coralSession, parameters);

        IncomingFeedResourceData feedData = IncomingFeedResourceData.getData(httpContext, feed);
        feedData.update(parameters);
        try
        {
            feed = updateResource(cmsData, coralSession, feedData, parameters);
        }
        catch(FeedCreationException e)
        {
            throw new ProcessingException("problem creating the feed", e);
        }
        catch(ProcessingException e)
        {
            throw e;
        }
        catch(Exception e)
        //catch(EmptyFeedNameException e)
        //catch(FeedAlreadyExistsException e)
        //catch(EmptyUrlException e)
        //catch(MalformedURIException e)
        //catch(InvalidResourceNameException e)
        {
            templatingContext.put("result", e.getClass().getSimpleName());
            return;
        }

        IncomingFeedResourceData.removeData(httpContext, null);
        templatingContext.put("result", successResult());
        mvcContext.setView("syndication.IncomingFeedList");
    }

    protected IncomingFeedResource getResource(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        return null;
    }

    protected IncomingFeedResource updateResource(CmsData cmsData, CoralSession coralSession,
        IncomingFeedResourceData feedData, Parameters parameters)
    throws EmptyFeedNameException, FeedCreationException, FeedAlreadyExistsException,
        EmptyUrlException, MalformedURIException, ProcessingException, InvalidResourceNameException  
    {
        IncomingFeedResource feed = syndicationService.getIncomingFeedsManager().createFeed(
            coralSession, feedData.getName(), feedData.getUrl(), feedData.getInterval(),
            feedData.getTemplate(), cmsData.getSite());
        if(feedData.getDescription() != null)
        {
            feed.setDescription(feedData.getDescription());
            feed.update();
        }
        return feed;
    }

    protected String successResult()
    {
        return "added_successfully";
    }

    public boolean checkAccessRights(Context context)
    throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.syndication.infeed.add");
    }
}
