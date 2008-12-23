/**
 * 
 */
package net.cyklotron.cms.modules.views.security;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;

public class SecurityServiceHelper
{
    private final SecurityService cmsSecurityService;
    
    public SecurityServiceHelper(SecurityService cmsSecurityService)
    {
        this.cmsSecurityService = cmsSecurityService;
    }
    
    public String getGroupName(RoleResource roleResource)
        throws CmsSecurityException
    {
        return cmsSecurityService.getShortGroupName(roleResource);
    }
}