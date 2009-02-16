package net.cyklotron.cms.syndication;

import java.io.IOException;
import java.util.List;

import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableException;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.TemplateNotFoundException;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * Manages the outgoing syndication feeds defined for the site..
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OutgoingFeedsManager.java,v 1.2 2005-08-08 09:07:56 rafal Exp $
 */
public interface OutgoingFeedsManager
{
    /** The parent node of the site outgoing feeds (outgoing) */
    public static final String OUTGOING_FEEDS_ROOT = "outgoing";
    
    /**
     * Create a feed for a site.
     *
     * @param name the name of the newly created feed.
     * @param interval refresh interval in minutes.
     * @param generationTemplate the template used for grneration of this feed.
     * @param publik determines if this feed is published publicly.
     * @param site the site resource for which feed is created.
     * @param subject subject performing the operation.
     * @throws FeedCreationException
     * @throws EmptyFeedNameException
     * @throws FeedAlreadyExistsException 
     * @throws EmptyDescriptionException 
     * @throws InvalidResourceNameException 
     */
    public OutgoingFeedResource createFeed(CoralSession coralSession, String name, String description,
        int interval, CategoryQueryResource categoryQuery, String generationTemplate,
        boolean publik, SiteResource site,
        String sortColumn, boolean sortOrder, int publicationTimeOffset, int limit)
    throws FeedCreationException, EmptyFeedNameException,
        FeedAlreadyExistsException, EmptyDescriptionException, InvalidResourceNameException;

    /**
     * Update a feed.
     *
     * @param feed the renamed feed
     * @param name the new name of the feed.
     * @param description the description of the feed presented for instance in RSS.
     * @param interval refresh interval in minutes.
     * @param categoryQuery the query executed to create a list of feed items.
     * @param generationTemplate the template used for grneration of this feed.
     * @param publik determines if this feed is published publicly.
     * @param subject subject performing the operation.
     * @throws EmptyFeedNameException
     * @throws FeedAlreadyExistsException 
     * @throws EmptyDescriptionException 
     * @throws InvalidResourceNameException 
     */
    public void updateFeed(CoralSession coralSession, OutgoingFeedResource feed, String name,
        String description, int interval, CategoryQueryResource categoryQuery, String generationTemplate, boolean publik,
        String sortColumn, boolean sortOrder, int publicationTimeOffset, int limit)
        throws EmptyFeedNameException, FeedAlreadyExistsException, EmptyDescriptionException, InvalidResourceNameException;
    
    /**
     * Delete a feed.
     * @param coralSession TODO
     * @param feed feed to be deleted
     *
     * @throws EntityInUseException
     */
    public void deleteFeed(CoralSession coralSession, OutgoingFeedResource feed)
    throws EntityInUseException;
    
    /**
     * Returns all feeds defined for the site
     * @param coralSession TODO
     * @param site the site resource for which feeds are defined.
     *
     * @throws SyndicationException thrown on problems with feeds retrieval. 
     */
    public OutgoingFeedResource[] getFeeds(CoralSession coralSession, SiteResource site)
    throws SyndicationException;

    /**
     * Returns a parent resource for feeds defined for the site
     * @param coralSession TODO
     * @param site the site resource for which feeds are defined.
     *
     * @throws CannotCreateIncomingFeedsRootException 
     * @throws TooManyIncomingFeedsRootsException 
     * @throws CannotCreateSyndicationRootException 
     * @throws TooManySyndicationRootsException 
     *
     */
    public Resource getFeedsParent(CoralSession coralSession, SiteResource site)
    throws TooManySyndicationRootsException, CannotCreateSyndicationRootException,
        TooManyFeedsRootsException, CannotCreateFeedsRootException;

    /**
     * Refreshes the feed content.
     *
     * @param feed the feed being updated.
     * @param subject the subject performing refresh action.
     * @throws CannotExecuteQueryException 
     * @throws TableException 
     * @throws CannotGenerateFeedException 
     * @throws MergingException 
     * @throws TemplateNotFoundException 
     */
    public void refreshFeed(Context context, CoralSession coralSession, OutgoingFeedResource feed)
    throws CannotExecuteQueryException, TableException, CannotGenerateFeedException,
        TemplateNotFoundException, MergingException, EntityDoesNotExistException;
    
    /**
     * Returns a list a list of available generation templates.
     * 
     * @return the list of templates feeds may generated by.
     * @throws IOException 
     */
    public List getGenerationTemplates() throws IOException;
}
