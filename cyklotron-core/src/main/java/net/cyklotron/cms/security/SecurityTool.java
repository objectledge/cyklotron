package net.cyklotron.cms.security;

import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.pool.RecyclableObject;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Role;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ContextTool;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.SchemaRoleResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * A context tool used for cms application.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SecurityTool.java,v 1.1 2005-01-12 20:44:49 pablo Exp $
 */
public class SecurityTool
    extends RecyclableObject
    implements ContextTool
{
    /** the rundata for future use */
    private RunData data;

    /** logging service */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;
    
    /** cms security service */
    private SecurityService cmsSecurityService;
    
    /** initialization flag. */
    private boolean initialized = false;
    
    // initialization ////////////////////////////////////////////////////////

    public void init(ServiceBroker broker, Configuration config)
    {
        if(!initialized)
        {
            log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
                getFacility("cms");
            resourceService = (ResourceService)broker.
                getService(ResourceService.SERVICE_NAME);
            cmsSecurityService = (SecurityService)broker.
                getService(SecurityService.SERVICE_NAME);
            initialized = true;
        }
    }

    public void prepare(RunData data)
    {
        this.data = data;
    }
    
    public void reset()
    {
        data = null;
    }
    
    // public interface ///////////////////////////////////////////////////////
    
    public RoleResource getRoleResource(SiteResource site, Role role)
    {
        if(role != null)
        {
            return cmsSecurityService.getRole(site, role);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * get arl role for specified schema role and resource.
     * 
     * @param schemaRole the schema role.
     * @param resource the resource.
     * @return the role
     */
    public Role getRole(SchemaRoleResource schemaRole, Resource resource)
        throws CmsSecurityException
    {
        String roleName = cmsSecurityService.roleNameFromSufix(schemaRole, resource);
        Role[] role = resourceService.getSecurity().getRole(roleName);
        if(role.length == 0)
        {
            return null;
        }
        if(role.length > 1)
        {
            throw new CmsSecurityException("Ambiguous role name: "+roleName);
        }
        return role[0];
    }
}

