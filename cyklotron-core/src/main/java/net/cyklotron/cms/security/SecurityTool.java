package net.cyklotron.cms.security;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.integration.SchemaRoleResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * A context tool used for cms application.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SecurityTool.java,v 1.3 2005-02-09 22:20:39 rafal Exp $
 */
public class SecurityTool
{
    /** logging service */
    private Logger log;

    /** cms security service */
    private SecurityService cmsSecurityService;
    
    /** context */
    private Context context;
    
    // initialization ////////////////////////////////////////////////////////

    public SecurityTool(Logger logger, SecurityService cmsSecuritySystem, Context context)
    {
        log = logger;
        this.cmsSecurityService = cmsSecuritySystem;
        this.context = context;
    }

    // public interface ///////////////////////////////////////////////////////
    
    public RoleResource getRoleResource(SiteResource site, Role role)
    {
        if(role != null)
        {
            CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
            return cmsSecurityService.getRole(coralSession, site, role);
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        String roleName = cmsSecurityService.roleNameFromSufix(schemaRole, resource);
        Role[] role = coralSession.getSecurity().getRole(roleName);
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

