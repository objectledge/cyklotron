package net.cyklotron.cms.syndication;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.site.SiteResource;

/**
 * This service manages the syndication feeds defined for the site..
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SyndicationService.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public interface SyndicationService
{
    /** The parent node of the site syndication (syndication) */
    public static final String SYNDICATION_ROOT = "syndication";

    public static final String NO_TEMPLATE_SELECTED_STRING = "";

    /**
     * Returns the incmonig feed manager.
     * 
     * @return the incoming feeds manager.
     */
    public IncomingFeedsManager getIncomingFeedsManager();

    /**
     * Returns the incmonig feed manager.
     * 
     * @return the incoming feeds manager.
     */
    public OutgoingFeedsManager getOutgoingFeedsManager();
    
    /** 
     * Returns a parent resource for syndication application defined for the site.
     * @param coralSession 
     *
     * @param site the site resource under which application is installed.
     * @throws TooManySyndicationRootsException thrown when the site has more then one syndication
     *  app root resource.
     * @throws CannotCreateSyndicationRootException thrown when there are problems creating
     *  the syndication app root.
     */
    public Resource getAppParent(CoralSession coralSession, SiteResource site)
        throws TooManySyndicationRootsException, CannotCreateSyndicationRootException;
}

