package net.cyklotron.cms.site;

import net.labeo.services.Service;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Provides information about deployed sites.
 *
 * @version $Id: SiteService.java,v 1.1 2005-01-12 20:44:43 pablo Exp $
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 */
public interface SiteService
    extends Service
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
    public SiteResource[] getSites();

    /**
     * Returns the available site templates.
     *
     * @return the available site templates.
     */
    public SiteResource[] getTemplates();

    /**
     * Returns the names of all defined virtual servers.
     *
     * @return the names of all defined virtual servers.
     */
    public String[] getVirtualServers();

    /**
     * Checks if a host name is one of the virtual servers recongized 
     * by the system.
     *
     * @param host the host name.
     * @return <code>true</code> if the host is one of the virtual servers 
     *         recongized by the system.
     */
    public boolean isVirtualServer(String host)
        throws SiteException;

    /**
     * Maps a virtual server name name name into a site.
     *
     * @param server the virtual server fully qualified domain name.
     * @return the site matching the alias name, or site "default" if not
     *         matched, or <code>null</code> if site default does not exit.
     */
    public SiteResource getSiteByAlias(String server)
        throws SiteException;

    /**
     * Maps a virtual server name name name into a site.
     *
     * @param server the virtual server fully qualified domain name.
     * @return the default navigation node that should be displayed 
     *         when this vritual server is requested.
     */
    public NavigationNodeResource getDefaultNode(String server)
        throws SiteException;
    
    /**
     * Returns virtual server mappings for a site.
     *
     * @param site the site.
     * @return the virtual server mappings.
     */
    public String[] getMappings(SiteResource site)
        throws SiteException;
    
    /**
     * Adds a virtual server mapping for a site.
     *
     * @param site the site.
     * @param server the virtual server name.
     * @param node the default navigation node that should be displayed  
     *        when this vritual server is requested.
     * @param subject the subject that performs the operation.
     * @throws SiteException if the server is already mapped to a site.
     */
    public void addMapping(SiteResource site, String server, NavigationNodeResource node,
                           Subject subject)
        throws SiteException;

    /**
     * Changes the default navigation node in a mapping.
     *
     * @param site the site.
     * @param server the virtual server name.
     * @param node the default navigation node that should be displayed  
     *        when this vritual server is requested.
     * @param subject the subject that performs the operation.
     * @throws SiteException if operation fails.
     */
    public void updateMapping(SiteResource site, String server, NavigationNodeResource node,
                           Subject subject)
        throws SiteException;
    
    /**
     * Removes a virtual server mapping.
     *
     * @param server the virtual server name.
     * @param subject the subject that performs the operation.
     * @throws SiteException if the server is not mapped to any site.
     */
    public void removeMapping(String server, Subject subject)
        throws SiteException;

    /**
     * Checks if a virtual server is primary for a site.
     *
     * @param server the virtual server name.
     * @return <code>true</code> if the mapping is declared as primary.
     */
    public boolean isPrimaryMapping(String server)
        throws SiteException;
    
    /**
     * Sets the mapping to be primary for a site.
     *
     * @param site the site.
     * @param server the virtual server name.
     * @param subject the subject that performs the operation.
     */
    public void setPrimaryMapping(SiteResource site, String server, Subject subject)
        throws SiteException;
    
    /**
     * Returns the primary mapping for a site.
     *
     * @param site the site.
     * @param return the name of the domain makred as primary, a name of one
     *        of the domains mapped to the site if none is marked, or
     *        <code>null</code> if no mappins for the site exist.
     */
    public String getPrimaryMapping(SiteResource site)
        throws SiteException;
    
    /**
     * Returns the site root node.
     *
     * @param name the site name.
     * @return site root resource, or <code>null</code> if no such site exists.
     */
    public SiteResource getSite(String name)
        throws SiteException;

    /**
     * Create a new site.
     *
     * @param template the site template.
     * @param name the site name.
     * @param description the site description.
     * @param subject the subject that performs the operation.
     * @return the newly created site.
     */
    public SiteResource createSite(SiteResource template, String name, 
                                   String description, Subject owner, Subject subject)
        throws SiteException;
    
    /**
     * Copy an existing, non-template site.
     *
     * @param source the site to copy.
     * @param destination the destination site name.
     * @param subject the subject that performs the operation.
     * @return the copy site object.
     */
    public SiteResource copySite(SiteResource source, String destination, Subject subject)
        throws SiteException;
    
    /**
     * Destroy a site.
     * 
     * <p>CAUTION. This operation cannot be undone.</p>
     *
     * @param name the site to destroy.
     * @param subject the subject that performs the operation.
     */
    public void destroySite(SiteResource site, Subject subject)
        throws SiteException;
}
