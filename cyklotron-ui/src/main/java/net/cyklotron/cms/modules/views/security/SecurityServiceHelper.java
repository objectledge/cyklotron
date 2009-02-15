/**
 * 
 */
package net.cyklotron.cms.modules.views.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;

public class SecurityServiceHelper
{
    private final SecurityService cmsSecurityService;

    private final CoralSession coralSession;

    public SecurityServiceHelper(SecurityService cmsSecurityService, CoralSession coralSession)
    {
        this.cmsSecurityService = cmsSecurityService;
        this.coralSession = coralSession;
    }

    public String getGroupName(RoleResource roleResource)
        throws CmsSecurityException
    {
        return cmsSecurityService.getShortGroupName(roleResource);
    }

    public Map<String, Object> getRoleDescription(Role role)
        throws Exception
    {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Resource> qRes = coralSession.getQuery().executeQuery(
            "FIND RESOURCE FROM cms.security.role WHERE role = " + role.getIdString()).getList(1);
        if(qRes.size() > 1)
        {
            result.put("special", "ambiguous");
        }
        else if(qRes.size() == 0)
        {
            result.put("special", "system");
        }
        else
        {            
            RoleResource roleResource = (RoleResource )qRes.get(0);
            result.put("role", roleResource);
            for(Resource r = roleResource.getParent(); r != null ; r = r.getParent())
            {
                if(r.getResourceClass().getName().equals("site.site"))
                {
                    result.put("site", r);
                    break;
                }
            }                
            if(cmsSecurityService.isGroupResource(roleResource))
            {
                result.put("group", cmsSecurityService.getShortGroupName(roleResource));
            }
        }
        return result;
    }
}
