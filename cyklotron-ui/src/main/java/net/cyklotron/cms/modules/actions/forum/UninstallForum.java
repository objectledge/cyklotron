/*
 */
package net.cyklotron.cms.modules.actions.forum;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.SecurityService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.Subject;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class UninstallForum 
    extends BaseCMSAction
{
    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            SecurityService cmsSecurityService = (SecurityService)data.
                getBroker().getService(SecurityService.SERVICE_NAME);
			Subject subject = coralSession.getUserSubject();
			// remove forum resources
			Resource[] discussions = coralSession.getStore().
						   getResourceByPath("/cms/sites/*/applications/forum/*");
			for (int i = 0; i < discussions.length; i++)
			{
				cmsSecurityService.cleanupRoles(discussions[i], true, subject);                
			}
            // remove forum role instances
            Resource[] forums = coralSession.getStore().
                getResourceByPath("/cms/sites/*/applications/forum");
            for (int i = 0; i < forums.length; i++)
            {
                Resource forum = forums[i];
                cmsSecurityService.cleanupRoles(forum, true, subject);                
            }
            // nuke existing forums
            deleteResources("/cms/sites/*/applications/forum");
    
            // Delete integration info
            deleteResources("/cms/applications/forum");
            
            // Delete workflow info
            deleteResources("/cms/workflow/automata/forum.discussion");
            deleteResources("/cms/workflow/automata/forum.message");
    
            // delete classes
            deleteResourceClass("cms.forum.message");
            deleteResourceClass("cms.forum.commentary");
            deleteResourceClass("cms.forum.discussion");
            deleteResourceClass("cms.forum.forum");
            deleteResourceClass("cms.forum.node");
            
            // delete permissions
            deletePermission("cms.forum.add");
            deletePermission("cms.forum.delete");
            deletePermission("cms.forum.view");
            deletePermission("cms.forum.moderate");
            deletePermission("cms.forum.modify");                        
        }
        catch(Exception e)
        {
            data.getContext().put("result", "exception");
            data.getContext().put("trace", StringUtils.stackTrace(e));
        }
    }
    
    /* 
     * (overriden)
     */
    public boolean checkAccess(RunData data) throws ProcessingException
    {
        return checkAdministrator(context, coralSession);
    }

    private void deleteResourceClass(String name)
        throws Exception
    {
        ResourceClass rc;
        try
        {
            rc = coralSession.getSchema().getResourceClass(name);
        }
        catch(EntityDoesNotExistException e)
        {
            return;
        }
        coralSession.getSchema().deleteResourceClass(rc);
    }
    
    private void deletePermission(String name)
        throws Exception
    {
        Permission[] p = coralSession.getSecurity().getPermission(name);
        for(int i=0; i<p.length; i++)
        {
            coralSession.getSecurity().deletePermission(p[i]);
        }
    }
    
    private void deleteResources(String path)
        throws Exception
    {
        Resource[] res = coralSession.getStore().getResourceByPath(path);
        for (int i = 0; i < res.length; i++)
        {
            coralSession.getStore().deleteTree(res[i]);
        }
    }
}
