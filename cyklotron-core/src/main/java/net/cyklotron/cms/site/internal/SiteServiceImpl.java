package net.cyklotron.cms.site.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.labeo.LabeoRuntimeException;
import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.event.EventService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;

import net.labeo.services.resource.CircularDependencyException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.EntityExistsException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.util.ObjectUtils;

import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteCopyingListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteDestructionListener;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.site.VirtualServerResource;
import net.cyklotron.cms.site.VirtualServerResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

/**
 * Provides information about deployed sites.
 *
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 * @version $Id: SiteServiceImpl.java,v 1.1 2005-01-12 20:44:35 pablo Exp $
 */
public class SiteServiceImpl
    extends BaseService
    implements SiteService
{
    // instance variables ////////////////////////////////////////////////////

    /** The eventService service. */
    private EventService eventService;

    /** The ResourceService. */
    private ResourceService resourceService;

    /** The StructureService. */
    private StructureService structureService;

    /** The logger. */
    protected LoggingFacility log;

    /** The /cms/sites node. */
    private Resource sites;

    /** The /cms/aliases node. */
    private Resource aliases;

    /** The listeners. This list is needed to keep permanent references to the
     *  listener object, to protect them from being GCed. */
    private ArrayList listeners = new ArrayList();

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void init()
    {
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        eventService = (EventService)broker.getService(EventService.SERVICE_NAME);
        structureService = (StructureService)broker.
            getService(StructureService.SERVICE_NAME);
        LoggingService loggingService = (LoggingService)broker.
            getService(LoggingService.SERVICE_NAME);
        log = loggingService.
            getFacility(LOGGING_FACILITY);
        Resource[] res =  resourceService.getStore().getResourceByPath("/cms/sites");
        if(res.length != 1)
        {
            throw new InitializationError("failed to lookup /cms/sites node");
        }
        sites = res[0];
        res =  resourceService.getStore().getResourceByPath("/cms/aliases");
        if(res.length != 1)
        {
            throw new InitializationError("failed to lookup /cms/aliases node");
        }
        aliases = res[0];

        String[] listenerClasses;
        int i = 0;
        try
        {
            listenerClasses = config.getStrings("listener.creation");
            if(listenerClasses != null)
            {
                for(i = 0; i < listenerClasses.length; i++)
                {
                    Object listener = ObjectUtils.instantiate(listenerClasses[i]);
                    listeners.add(listener);
                    eventService.addListener(SiteCreationListener.class,listener,null);
                }
            }
            listenerClasses = config.getStrings("listener.copying");
            if(listenerClasses != null)
            {
                for(i = 0; i < listenerClasses.length; i++)
                {
                    Object listener = ObjectUtils.instantiate(listenerClasses[i]);
                    listeners.add(listener);
                    eventService.addListener(SiteCopyingListener.class,listener,null);
                }
            }
            listenerClasses = config.getStrings("listener.destruction");
            if(listenerClasses != null)
            {
                for(i = 0; i < listenerClasses.length; i++)
                {
                    Object listener = ObjectUtils.instantiate(listenerClasses[i]);
                    listeners.add(listener);
                    eventService.addListener(SiteDestructionListener.class,listener,null);
                }
            }
        }
        catch(LabeoRuntimeException e)
        {
            throw new InitializationError("failed to instantiate listeners", e);
        }
    }

    // SiteService interface /////////////////////////////////////////////////

    /**
     * Returns the deployed sites.
     *
     * @return the deployed sites.
     */
    public SiteResource[] getSites()
    {
        Resource[] res = resourceService.getStore().getResource(sites);
        ArrayList temp = new ArrayList();
        for(int i=0; i<res.length; i++)
        {
            if(!((SiteResource)res[i]).getTemplate())
            {
                temp.add(res[i]);
            }
        }
        SiteResource[] result = new SiteResource[temp.size()];
        temp.toArray(result);
        return result;
    }

    /**
     * Returns the available site templates.
     *
     * @return the available site templates.
     */
    public SiteResource[] getTemplates()
    {
        Resource[] res = resourceService.getStore().getResource(sites);
        ArrayList temp = new ArrayList();
        for(int i=0; i<res.length; i++)
        {
            if(((SiteResource)res[i]).getTemplate())
            {
                temp.add(res[i]);
            }
        }
        SiteResource[] result = new SiteResource[temp.size()];
        temp.toArray(result);
        return result;
    }

    /**
     * Returns the names of all defined virtual servers.
     *
     * @return the names of all defined virtual servers.
     */
    public String[] getVirtualServers()
    {
        Resource[] res = resourceService.getStore().getResource(aliases);
        ArrayList temp = new ArrayList();
        for(int i=0; i<res.length; i++)
        {
            temp.add(res[i].getName());
        }
        String[] result = new String[temp.size()];
        temp.toArray(result);
        return result;
    }

    /**
     * Checks if a host name is one of the virtual servers recongized 
     * by the system.
     *
     * @param host the host name.
     * @return <code>true</code> if the host is one of the virtual servers 
     *         recongized by the system.
     */
    public boolean isVirtualServer(String host)
        throws SiteException
    {
        Resource[] res = resourceService.getStore().getResource(aliases, host);
        return res.length > 0;
    }

    /**
     * Maps a virtual server name name name into a site.
     *
     * @param server the virtual server fully qualified domain name.
     * @return the site matching the alias name, or site "default" if not
     *         matched, or <code>null</code> if site default does not exit.
     */
    public SiteResource getSiteByAlias(String server)
        throws SiteException
    {
        Resource[] match = resourceService.getStore().getResource(aliases, server);
        if(match.length == 1)
        {
            SiteResource site = (SiteResource)((VirtualServerResource)match[0]).getSite();
            if(!site.getTemplate())
            {
                return site;
            }
        }
        return getSite("default");
    }

    /**
     * Maps a virtual server name name name into a site.
     *
     * @param server the virtual server fully qualified domain name.
     * @return the default navigation node that should be displayed
     *         when this vritual server is requested.
     */
    public NavigationNodeResource getDefaultNode(String server)
        throws SiteException
    {
        Resource[] match = resourceService.getStore().getResource(aliases, server);
        if(match.length == 1)
        {
            VirtualServerResource virtual = (VirtualServerResource)match[0];
            if(!virtual.getSite().getTemplate())
            {
                return virtual.getNode();
            }
        }
        SiteResource site = getSite("default");
        if(site == null)
        {
            return null;
        }
        try
        {
            return structureService.getRootNode(site);
        }
        catch(StructureException e)
        {
            throw new SiteException("failed to locate the root navigation node", e);
        }
    }

    /**
     * Returns virtual server mappings for a site.
     *
     * @param site the site.
     * @return the virtual server mappings.
     */
    public String[] getMappings(SiteResource site)
        throws SiteException
    {
        if(site.getTemplate())
        {
            throw new SiteException("no mappings are allowed for site templates");
        }

        Resource[] servers = resourceService.getStore().getResource(aliases);
        ArrayList temp = new ArrayList();
        for(int i=0; i<servers.length; i++)
        {
            if(((VirtualServerResource)servers[i]).getSite().equals(site))
            {
                temp.add(servers[i].getName());
            }
        }
        String[] result = new String[temp.size()];
        temp.toArray(result);
        return result;
    }

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
    public void addMapping(SiteResource site, String server,
                           NavigationNodeResource node, Subject subject)
        throws SiteException
    {
        Resource[] res = resourceService.getStore().getResource(aliases, server);
        if(res.length > 1)
        {
            throw new SiteException("multiple mappings for "+server);
        }
        if(res.length != 0)
        {
            throw new SiteException(server+" is already mapped to "+
                                    ((VirtualServerResource)res[0]).getSite().getName());
        }
        try
        {
            VirtualServerResourceImpl.
                createVirtualServerResource(resourceService, server, aliases, site, node, false, subject);
        }
        catch(ValueRequiredException e)
        {
            throw new SiteException("ARL exception", e);
        }
    }


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
        throws SiteException
    {
        Resource[] res = resourceService.getStore().getResource(aliases, server);
        if(res.length == 0)
        {
            throw new SiteException("no mapping exists for "+server);
        }
        if(res.length > 1)
        {
            throw new SiteException("multiple mappings for "+server);
        }
        try
        {
            ((VirtualServerResource)res[0]).setNode(node);
            res[0].update(subject);
        }
        catch(Exception e)
        {
            throw new SiteException("ARL exception", e);
        }
    }

    /**
     * Removes a virtual server mapping.
     *
     * @param server the virtual server name.
     * @param subject the subject that performs the operation.
     * @throws SiteException if the server is not mapped to any site.
     */
    public void removeMapping(String server, Subject subject)
        throws SiteException
    {
        Resource[] res = resourceService.getStore().getResource(aliases, server);
        if(res.length == 0)
        {
            throw new SiteException("no mapping exists for "+server);
        }
        if(res.length > 1)
        {
            throw new SiteException("multiple mappings for "+server);
        }
        try
        {
            resourceService.getStore().deleteResource(res[0]);
        }
        catch(EntityInUseException e)
        {
            throw new SiteException("ARL exception", e);
        }
    }

    /**
     * Checks if a virtual server is primary for a site.
     *
     * @param site the site.
     * @param server the virtual server name.
     * @return <code>true</code> if the mapping is declared as primary.
     */
    public boolean isPrimaryMapping(String server)
        throws SiteException
    {
        Resource[] res = resourceService.getStore().getResource(aliases, server);
        if(res.length == 0)
        {
            throw new SiteException("no mapping exists for "+server);
        }
        if(res.length > 1)
        {
            throw new SiteException("multiple mappings for "+server);
        }
        return ((VirtualServerResource)res[0]).getPrimary();
    }

    /**
     * Sets the mapping to be primary for a site.
     *
     * @param site the site.
     * @param server the virtual server name.
     * @param subject the subject that performs the operation.
     */
    public void setPrimaryMapping(SiteResource site, String server, Subject subject)
        throws SiteException
    {
        Resource[] mappings = resourceService.getStore().getResource(aliases);
        for(int i=0; i<mappings.length; i++)
        {
            VirtualServerResource alias = (VirtualServerResource)mappings[i];
            if(alias.getSite().equals(site))
            {
                if(alias.getName().equals(server))
                {
                    alias.setPrimary(true);
                }
                else
                {
                    alias.setPrimary(false);
                }
            }
            alias.update(subject);
        }
    }
    
    /**
     * Returns the primary mapping for a site.
     *
     * @param site the site.
     * @param return the name of the domain makred as primary, a name of one
     *        of the domains mapped to the site if none is marked, or
     *        <code>null</code> if no mappins for the site exist.
     */
    public String getPrimaryMapping(SiteResource site)
        throws SiteException
    {
        Resource[] mappings = resourceService.getStore().getResource(aliases);
        VirtualServerResource randomAlias = null;
        for(int i=0; i<mappings.length; i++)
        {
            VirtualServerResource alias = (VirtualServerResource)mappings[i];
            if(alias.getSite().equals(site))
            {
                randomAlias = alias;
                if(alias.getPrimary())
                {
                    return alias.getName();
                }
            }
        }
        if(randomAlias != null)
        {
            return randomAlias.getName();
        }
        else
        {
            return null;
        }
    }    
    
    /**
     * Returns the site root node.
     *
     * @param name the site name.
     * @return site root resource, or <code>null</code> if no such site exists.
     */
    public SiteResource getSite(String name)
        throws SiteException
    {
        Resource[] res = resourceService.getStore().getResource(sites, name);
        if(res.length != 1)
        {
            return null;
        }
        return (SiteResource)res[0];
    }

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
        throws SiteException
    {
        if(!template.getTemplate())
        {
            throw new SiteException(template.getName()+" is not a site template");
        }
        Resource[] check = resourceService.getStore().getResource(sites, name);
        if(check.length != 0)
        {
            throw new SiteException("site "+name+" already exists");
        }
        try
        {
            resourceService.getStore().copyTree(template, sites, name, owner);
        }
        catch(CircularDependencyException e)
        {
            throw new SiteException("cannot create site", e);
        }
        check = resourceService.getStore().getResource(sites, name);
        if(check.length != 1)
        {
            throw new SiteException("site was not created properly");
        }
        SiteResource site = (SiteResource)check[0];
        site.setTemplate(false);
        site.setDescription(description);
        site.update(subject);
        resourceService.getStore().setOwner(site, owner);
        setupSecurity(site, owner);
        // notify listeners
        try
        {
            Method method = SiteCreationListener.class.
                getMethod("createSite", new Class[] { String.class, String.class });
            Object[] args = { template.getName(), name};
            eventService.fireEvent(method, args, null);
        }
        catch(NoSuchMethodException e)
        {
            throw new SiteException("Incompatible change of SiteCopyingListener interface", e);
        }
        return site;
    }

    /**
     * Copy an existing, non-template site.
     *
     * @param source the site to copy.
     * @param destination the destination site name.
     * @param subject the subject that performs the operation.
     * @return the copy site object.
     */
    public SiteResource copySite(SiteResource source, String destination, Subject subject)
        throws SiteException
    {
        Resource[] check = resourceService.getStore().getResource(sites, destination);
        if(check.length != 0)
        {
            throw new SiteException("site "+destination+" already exists");
        }
        try
        {
            resourceService.getStore().copyTree(source, sites, destination, subject);
        }
        catch(CircularDependencyException e)
        {
            throw new SiteException("cannot copy site", e);
        }
        // notify listeners
        try
        {
            Method method = SiteCopyingListener.class.
                getMethod("copySite", new Class[] { String.class, String.class });
            Object[] args = { source.getName(), destination};
            eventService.fireEvent(method, args, null);
        }
        catch(NoSuchMethodException e)
        {
            throw new SiteException("Incompatible change of SiteCopyingListener interface", e);
        }
        check = resourceService.getStore().getResource(sites, destination);
        return (SiteResource)check[0];
    }

    /**
     * Destroy a site.
     *
     * <p>CAUTION. This operation cannot be undone.</p>
     *
     * @param site the site to destroy.
     * @param subject the subject that performs the operation.
     */
    public void destroySite(SiteResource site, Subject subject)
        throws SiteException
    {
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Sets up the roles and permissions needed for administering the site.
     *
     * @param site the site.
     * @param owner the site's owner.
     */
    protected void setupSecurity(SiteResource site, Subject owner)
    {
        SecurityService cmsSecurityService = (SecurityService)broker.
            getService(SecurityService.SERVICE_NAME);
        String name = site.getName();
        try
        {
            Subject root = resourceService.getSecurity().getSubject(Subject.ROOT);
            Role masterAdmin = resourceService.getSecurity().getUniqueRole("cms.administrator");

            Role teamMember = resourceService.getSecurity().
                createRole("cms.site.team_member."+name);
            Role administrator = resourceService.getSecurity().
                createRole("cms.site.administrator."+name);
            Role layoutAdministrator = resourceService.getSecurity().
                createRole("cms.layout.administrator."+name);
            Role siteRole = resourceService.getSecurity().
                createRole("cms.site.siterole."+name);

            resourceService.getSecurity().addSubRole(masterAdmin, administrator);
            resourceService.getSecurity().addSubRole(administrator, layoutAdministrator);
            
            resourceService.getSecurity().
                grant(teamMember, owner, true, root);
            resourceService.getSecurity().
                grant(administrator, owner, true, root);
            
            cmsSecurityService.
                registerRole(site, teamMember, null, false,
                             false, "cms.site.team_member", null, owner);

            // register the site team as a workgroup
            Role workgroup = resourceService.getSecurity().
                getUniqueRole("cms.workgroup");
            resourceService.getSecurity().addSubRole(workgroup, teamMember);

            RoleResource administratorRole = cmsSecurityService.
                registerRole(site, administrator, null, false, false,
                            "cms.site.administrator", null, owner);
            cmsSecurityService.
                registerRole(site, layoutAdministrator, null, false, false,
                             "cms.layout.administrator", administratorRole, owner);

            site.setTeamMember(teamMember);
            site.setAdministrator(administrator);
            site.setLayoutAdministrator(layoutAdministrator);
            site.setSiteRole(siteRole);
            site.update(owner);

            Permission siteAdminister = resourceService.getSecurity().
                getUniquePermission("cms.site.administer");
            Permission layoutAdminister = resourceService.getSecurity().
                getUniquePermission("cms.layout.administer");
            resourceService.getSecurity().
                grant(site, administrator, siteAdminister, true, root);
            resourceService.getSecurity().
                grant(site, layoutAdministrator, layoutAdminister, true, root);

            Role nodeAdministrator = cmsSecurityService.
                createRole(administrator, "cms.structure.administrator", structureService.getRootNode(site), root);
            resourceService.getSecurity().
                grant(nodeAdministrator, owner, true, root);
            Role visitor = cmsSecurityService.
                createRole(administrator, "cms.structure.visitor", structureService.getRootNode(site), root);
            resourceService.getSecurity().
                addSubRole(teamMember, visitor);
        }
        catch(Exception e)
        {
            log.error("failed to setup security for site "+name, e);
        }
    }
}
