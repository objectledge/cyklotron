package net.cyklotron.cms.link;

import java.util.List;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: LinkService.java,v 1.4 2005-02-09 22:20:42 rafal Exp $
 */
public interface LinkService
{
    /** The name of the service (<code>"link"</code>). */
    public final static String SERVICE_NAME = "link";

    /**
     * The logging facility where the service issues it's informational
     * messages.
     */
    public static final String LOGGING_FACILITY = "link";

    /**
     * return the links root node.
     *
     * @param site the site resource.
     * @return the links root resource.
     * @throws LinkException if the operation fails.
     */
    public LinkRootResource getLinkRoot(CoralSession coralSession, SiteResource site)
        throws LinkException;

    /**
     * return the list of links.
     *
     * @param linkRoot the links pool.
     * @param config the configuration.
     * @return the links list.
     * @throws LinkException if the operation fails.
     */
    public List getLinks(CoralSession coralSession, LinkRootResource linkRoot, Parameters config)
        throws LinkException;

    /**
     * return the list of pools.
     *
     * @param linkRoot the links pool.
     * @return the links list.
     */
    public List getPools(CoralSession coralSession,LinkRootResource linkRoot);

    /**
     * notify that link was clicked.
     *
     * @param link the link that is being clicked.
     */
    public void followLink(CoralSession coralSession, BaseLinkResource link);

    /**
     * delete the link.
     *
     * @param link the link.
     */
    public void deleteLink(CoralSession coralSession, BaseLinkResource link)
        throws LinkException;

	/**
	 * Copy the link.
	 *
	 * @param source the source link.
	 * @param targetName the name of the new link.
	 * @param parent the target pool.
	 * @param subject the subject.
	 * @return the copied link.
	 * @throws LinkException if the operation fails.
	 */
	public BaseLinkResource copyLink(CoralSession coralSession, BaseLinkResource source, String targetName, PoolResource parent, Subject subject)
		throws LinkException;
		
    /**
     * execute logic of the job to check expiration date.
     */
    public void checkLinkState(CoralSession coralSession);

}
