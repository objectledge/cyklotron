/*
 * Created on Dec 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.cyklotron.cms.modules.views.security;

import net.labeo.services.resource.Role;
import net.labeo.webcore.RunData;

/**
 * @author fil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddUser
    extends BaseSecurityScreen
{
    public boolean checkAccess(RunData data)
    {
        if(cmsSecurityService.getAllowAddUser())
        {
            return true;
        }
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
    
    public boolean requiresLogin(RunData data)
    {
        return !cmsSecurityService.getAllowAddUser();
    }
}
