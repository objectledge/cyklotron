package net.cyklotron.cms.syndication.internal;

import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.syndication.CannotCreateFeedsRootException;
import net.cyklotron.cms.syndication.CannotCreateSyndicationRootException;
import net.cyklotron.cms.syndication.EmptyFeedNameException;
import net.cyklotron.cms.syndication.EmptyUrlException;
import net.cyklotron.cms.syndication.FeedAlreadyExistsException;
import net.cyklotron.cms.syndication.FeedCreationException;
import net.cyklotron.cms.syndication.SyndicationException;
import net.cyklotron.cms.syndication.SyndicationService;
import net.cyklotron.cms.syndication.TooManyFeedsRootsException;
import net.cyklotron.cms.syndication.TooManySyndicationRootsException;
import net.cyklotron.cms.util.URI;

/**
 * Base feeds manager.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseFeedsManager.java,v 1.2 2007-11-18 21:23:22 rafal Exp $
 */
public abstract class BaseFeedsManager
{
    protected SyndicationService syndicationService;
    protected FileSystem fileSystem;
    
    public BaseFeedsManager(SyndicationService syndicationService, FileSystem fileSystem)
    {
        this.syndicationService = syndicationService;
        this.fileSystem = fileSystem;
    }
    
    protected abstract Resource getFeedsParent(CoralSession coralSession, SiteResource site)
    throws TooManySyndicationRootsException, CannotCreateSyndicationRootException,
        TooManyFeedsRootsException, CannotCreateFeedsRootException;

    protected synchronized Resource getFeedsParent(SiteResource site, String name, CoralSession coralSession)
    throws TooManySyndicationRootsException, CannotCreateSyndicationRootException,
        TooManyFeedsRootsException, CannotCreateFeedsRootException
    {
        Resource parent = syndicationService.getAppParent(coralSession, site);
        Resource[] res = coralSession.getStore().getResource(parent, name);
        if(res.length > 1)
        {
            throw new TooManyFeedsRootsException("more than one '"+name+
                "' root found for site '"+site+"'");
        }
        else if(res.length == 0)
        {
            try
            {
                return CmsNodeResourceImpl.createCmsNodeResource(coralSession, name, parent);
            }
            catch(InvalidResourceNameException e)
            {
                throw new CannotCreateFeedsRootException("wrong name", e);
            }
        }
        return res[0];
    }

    public Resource prepareCreateFeed(CoralSession coralSession, String name, SiteResource site)
    throws EmptyFeedNameException, FeedCreationException, FeedAlreadyExistsException
    {
        checkName(name);
        
        Resource parent;
        try
        {
            parent = getFeedsParent(coralSession, site);
        }
        catch(SyndicationException e)
        {
            throw new FeedCreationException("cannot get parent resource", e);
        }
        
        checkExists(name, parent, coralSession);
        
        return parent;
    }
    
    protected void prepareRenameFeedResource(CoralSession coralSession, Resource feed, String name)
    throws EmptyFeedNameException, FeedAlreadyExistsException
    {
        checkName(name);
        
        Resource parent = feed.getParent();

        Resource[] res = coralSession.getStore().getResource(parent, name);
        if(res.length == 1 && res[0].equals(feed))
        {
            return;
        }
        else if(res.length > 0)
        {
            throw new FeedAlreadyExistsException(name);
        }
    }
    
    public void deleteFeedResource(CoralSession coralSession, Resource feed)
    throws EntityInUseException
    {
        coralSession.getStore().deleteResource(feed);
    }

    protected void checkName(String name) throws EmptyFeedNameException
    {
        if(name == null || name.length() == 0)
        {
            throw new EmptyFeedNameException();
        }
    }

    protected void checkExists(String name, Resource parent, CoralSession coralSession) throws FeedAlreadyExistsException
    {
        Resource[] res = coralSession.getStore().getResource(parent, name);
        if(res.length > 0)
        {
            throw new FeedAlreadyExistsException(name);
        }
    }

    protected void checkUrl(String url) throws EmptyUrlException, URI.MalformedURIException
    {
        if(url == null || url.equals(""))
        {
            throw new EmptyUrlException();
        }
        new URI(url);
    }
    
    protected String fixTemplate(String template)
    {
        if(template != null && template.equals(SyndicationService.NO_TEMPLATE_SELECTED_STRING))
        {
            template = null;
        }
        return template;
    }
}
