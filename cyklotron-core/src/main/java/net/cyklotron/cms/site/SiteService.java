package net.cyklotron.cms.site;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Provides information about deployed sites.
 *
 * @version $Id: SiteService.java,v 1.8 2007-11-18 21:23:05 rafal Exp $
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 */
public interface SiteService
{
    /** The name of the service (<code>"structure"</code>). */
    public final static String SERVICE_NAME = "site";

    /** The logging facility where the service issues it's informational
     * messages. */
    public static final String LOGGING_FACILITY = "site";

    /**
     * Returns the deployed sites.
     *
     * @return the deployed sites.
     */
    public SiteResource[] getSites(CoralSession coralSession);

    public Resource getSitesRoot(CoralSession coralSession);
    
    /**
     * Returns the available site templates.
     *
     * @return the available site templates.
     */
    public SiteResource[] getTemplates(CoralSession coralSession);

    /**
     * Returns the names of all defined virtual servers.
     *
     * @return the names of all defined virtual servers.
     */
    public String[] getVirtualServers(CoralSession coralSession);

    /**
     * Checks if a host name is one of the virtual servers recongized 
     * by the system.
     *
     * @param host the host name.
     * @return <code>true</code> if the host is one of the virtual servers 
     *         recongized by the system.
     */
    public boolean isVirtualServer(CoralSession coralSession, String host)
        throws SiteException;

    /**
     * Maps a virtual server name name name into a site.
     *
     * @param server the virtual server fully qualified domain name.
     * @return the site matching the alias name, or site "default" if not
     *         matched, or <code>null</code> if site default does not exit.
     */
    public SiteResource getSiteByAlias(CoralSession coralSession, String server)
        throws SiteException;

    /**
     * Maps a virtual server name name name into a site.
     *
     * @param server the virtual server fully qualified domain name.
     * @return the default navigation node that should be displayed 
     *         when this vritual server is requested.
     */
    public NavigationNodeResource getDefaultNode(CoralSession coralSession, String server)
        throws SiteException;
    
    /**
     * Returns virtual server mappings for a site.
     *
     * @param site the site.
     * @return the virtual server mappings.
     */
    public String[] getMappings(CoralSession coralSession, SiteResource site)
        throws SiteException;
    
    /**
     * Adds a virtual server mapping for a site.
     *
     * @param site the site.
     * @param server the virtual server name.
     * @param node the default navigation node that should be displayed  
     *        when this vritual server is requested.
     * @throws SiteException if the server is already mapped to a site.
     * @throws InvalidResourceNameException if the server argument contains invalid characters.
     */
    public void addMapping(CoralSession coralSession, SiteResource site, String server, NavigationNodeResource node)
        throws SiteException, InvalidResourceNameException;

    /**
     * Changes the default navigation node in a mapping.
     *
     * @param site the site.
     * @param server the virtual server name.
     * @param node the default navigation node that should be displayed  
     *        when this vritual server is requested.
     * @throws SiteException if operation fails.
     */
    public void updateMapping(CoralSession coralSession, SiteResource site, String server, NavigationNodeResource node)
        throws SiteException;
    
    /**
     * Removes a virtual server mapping.
     *
     * @param server the virtual server name.
     * @throws SiteException if the server is not mapped to any site.
     */
    public void removeMapping(CoralSession coralSession, String server)
        throws SiteException;

    /**
     * Checks if a virtual server is primary for a site.
     *
     * @param server the virtual server name.
     * @return <code>true</code> if the mapping is declared as primary.
     */
    public boolean isPrimaryMapping(CoralSession coralSession, String server)
        throws SiteException;
    
    /**
     * Sets the mapping to be primary for a site.
     *
     * @param site the site.
     * @param server the virtual server name.
     */
    public void setPrimaryMapping(CoralSession coralSession, SiteResource site, String server)
        throws SiteException;
    
    /**
     * Returns the primary mapping for a site.
     *
     * @param site the site.
     * @return the name of the domain makred as primary, a name of one
     *        of the domains mapped to the site if none is marked, or
     *        <code>null</code> if no mappins for the site exist.
     */
    public String getPrimaryMapping(CoralSession coralSession, SiteResource site)
        throws SiteException;
    
    /**
     * Returns the site root node.
     *
     * @param name the site name.
     * @return site root resource, or <code>null</code> if no such site exists.
     */
    public SiteResource getSite(CoralSession coralSession, String name)
        throws SiteException;

    /**
     * Create a new site.
     *
     * @param template the site template.
     * @param name the site name.
     * @param requiresSecureChannel is secure channel (SSL connection) required to browse the site.
     * @param description the site description.
     * @return the newly created site.
     */
    public SiteResource createSite(CoralSession coralSession, SiteResource template, String name, 
                                   String description, boolean requiresSecureChannel, Subject owner)
        throws SiteException, InvalidResourceNameException;
    
    /**
     * Copy an existing, non-template site.
     *
     * @param source the site to copy.
     * @param destination the destination site name.
     * @return the copy site object.
     */
    public SiteResource copySite(CoralSession coralSession, SiteResource source, String destination)
        throws SiteException, InvalidResourceNameException;
    
    /**
     * Destroy a site.
     * 
     * <p>CAUTION. This operation cannot be undone.</p>
     *
     * @param site the site to destroy.
     */
    public void destroySite(CoralSession coralSession, SiteResource site)
        throws SiteException;
}
