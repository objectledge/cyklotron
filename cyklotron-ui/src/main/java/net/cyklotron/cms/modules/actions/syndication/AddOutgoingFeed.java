package net.cyklotron.cms.modules.actions.syndication;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.syndication.EmptyDescriptionException;
import net.cyklotron.cms.syndication.EmptyFeedNameException;
import net.cyklotron.cms.syndication.FeedAlreadyExistsException;
import net.cyklotron.cms.syndication.FeedCreationException;
import net.cyklotron.cms.syndication.OutgoingFeedResource;
import net.cyklotron.cms.syndication.OutgoingFeedResourceData;
import net.cyklotron.cms.syndication.SyndicationService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 * Action for adding outgoing feeds to the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddOutgoingFeed.java,v 1.3 2005-08-08 09:07:29 rafal Exp $
 */
public class AddOutgoingFeed extends BaseSyndicationAction
{
    protected CategoryQueryService categoryQueryService;
    
    public AddOutgoingFeed(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SyndicationService syndicationService,
        CategoryQueryService categoryQueryService)
    {
        super(logger, structureService, cmsDataFactory, syndicationService);
        this.categoryQueryService = categoryQueryService;        
    }
    
    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        
        OutgoingFeedResource feed = getResource(coralSession, parameters);

        OutgoingFeedResourceData feedData = OutgoingFeedResourceData.getData(httpContext, feed);
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
        //catch(EmptyDescriptionException e)
        //catch(InvalidResourceNameException e)
        {
            templatingContext.put("result", e.getClass().getSimpleName());
            return;
        }

        OutgoingFeedResourceData.removeData(httpContext, null);
        templatingContext.put("result", successResult());
        mvcContext.setView("syndication.OutgoingFeedList");
    }

    protected OutgoingFeedResource getResource(CoralSession coralSession, Parameters parameters)
    throws ProcessingException
    {
        return null;
    }

    protected OutgoingFeedResource updateResource(CmsData cmsData, CoralSession coralSession, OutgoingFeedResourceData feedData, Parameters parameters)
    throws EmptyFeedNameException, FeedCreationException, FeedAlreadyExistsException,
        EmptyDescriptionException, ProcessingException, InvalidResourceNameException  
    {
        OutgoingFeedResource feed = syndicationService.getOutgoingFeedsManager().createFeed(
            coralSession, feedData.getName(), feedData.getDescription(), feedData.getInterval(),
            getCategoryQuery(cmsData, coralSession, feedData.getQueryName()),
            feedData.getTemplate(), feedData.getPublic(), cmsData.getSite(),
            feedData.getSortColumn(), feedData.getSortOrder(), feedData.getPublicationTimeOffset(),
            feedData.getMaxResNumber());
        
        updateOptionalFields(feedData, feed);
        
        return feed;
    }

    protected void updateOptionalFields(OutgoingFeedResourceData feedData, OutgoingFeedResource feed)
    {
        if(feed.getCategory() == null
            || !feed.getCategory().equals(feedData.getCategory()))
        {
            feed.setCategory(feedData.getCategory());
        }
        if(feed.getCopyright() == null
            || !feed.getCopyright().equals(feedData.getCopyright()))
        {
            feed.setCopyright(feedData.getCopyright());
        }
        if(feed.getLanguage() == null
            || !feed.getLanguage().equals(feedData.getLang()))
        {
            feed.setLanguage(feedData.getLang());
        }
        if(feed.getManagingEditor() == null
            || !feed.getManagingEditor().equals(feedData.getManagingEditor()))
        {
            feed.setManagingEditor(feedData.getManagingEditor());
        }
        if(feed.getWebMaster() == null
            || !feed.getWebMaster().equals(feedData.getWebmaster()))
        {
            feed.setWebMaster(feedData.getWebmaster());
        }

        feed.update();
    }

    protected CategoryQueryResource getCategoryQuery(CmsData cmsData, CoralSession coralSession, String queryName)
    throws ProcessingException
    {
        if(queryName != null)
        {
            Resource parent;
            try
            {
                parent = categoryQueryService.getCategoryQueryRoot(coralSession, cmsData.getSite());
            }
            catch(CategoryQueryException e)
            {
                throw new ProcessingException("cannot get category query root", e);
            }
            Resource[] queries = coralSession.getStore().getResource(parent, queryName);
            if(queries.length == 1)
            {
                return (CategoryQueryResource) queries[0];
            }
            else
            {
                throw new ProcessingException("duplicate category queries named '"+queryName+"'");
            }
        }
        return null;
    }

    protected String successResult()
    {
        return "added_successfully";
    }

    public boolean checkAccessRights(Context context)
    throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.syndication.outfeed.add");
    }
}
