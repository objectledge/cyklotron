package net.cyklotron.cms.modules.actions.fixes;

import java.util.ArrayList;
import net.labeo.util.StringUtils;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.PermissionAssignment;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.RoleAssignment;
import net.labeo.services.resource.RoleImplication;
import net.labeo.services.resource.Subject;

import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class UninstallAll
    extends BaseCMSAction
{
    private SecurityService cmsSecurityService = null;

    protected Logger log;

    private ArrayList roles;
    
    public UninstallAll()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("fixes");
        cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);
        roles = new ArrayList();
    }
    
    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            Subject subject = coralSession.getUserSubject();
            emptyTrash(subject);
            clearSecurity(subject);
            uninstallOldRelationsips(subject);
            uninstallForum(subject);
            uninstallBanner(subject);
            uninstallPoll(subject);
            uninstallLink(subject);
            uninstallSearch(subject);
            emptyTrash(subject);
            deleteRoles();
        }
        catch(Exception e)
        {
            //data.getContext().put("result", "exception");
            //data.getContext().put("trace", StringUtils.stackTrace(e));
            log.error("Error on uninstall: try again!!!",e);
            execute(data);
        }
    }

    public void emptyTrash(Subject subject)
        throws Exception
    {
        // remove forum role instances
        deleteResources("/tmp/*");
    }
    
    public void clearSecurity(Subject subject)
        throws Exception
    {
        Resource[] security = coralSession.getStore()
            .getResourceByPath("/cms/sites/*/security");
        for(int i = 0; i < security.length; i++)
        {
            clearNode(security[i], subject);
        }
    }
    
    private void clearNode(Resource resource, Subject subject)
        throws Exception
    {
        
        Resource[] children = coralSession.getStore()
            .getResource(resource);
        for(int i = 0; i < children.length; i++)
        {
            clearNode(children[i], subject);
        }
        if(resource.getName().equals("security") ||
           resource.getName().startsWith("cms.site") ||
           resource.getName().startsWith("cms.layout") ||
           resource.getName().startsWith("cms.rich") ||
           resource.getName().startsWith("cms.structure"))
        {
            return;
        }
        System.out.println("Clear role: "+resource.getName());
        if(resource instanceof RoleResource)
        {
            Role role = ((RoleResource)resource).getRole();
            PermissionAssignment[] pa = role.getPermissionAssignments();
            for(int i = 0; i < pa.length; i++)
            {
                coralSession.getSecurity().revoke(pa[i].getResource(), role, pa[i].getPermission(), subject);
            }
            RoleAssignment[] ra = role.getRoleAssignments();
            for(int i = 0; i < ra.length; i++)
            {
                coralSession.getSecurity().revoke(role, ra[i].getSubject(), subject);
            }
            RoleImplication[] ri = role.getImplications();
            for(int i = 0; i < ri.length; i++)
            {
                coralSession.getSecurity().deleteSubRole(ri[i].getSuperRole(), ri[i].getSubRole());
            }
            coralSession.getStore().deleteResource(resource);
            try
            {
                coralSession.getSecurity().deleteRole(role);
            }
            catch(Exception e)
            {
                roles.add(role);
            }
        }
    }

    public void deleteRoles()
        throws Exception
    {
        //Role[] role = coralSession.getSecurity().getRole("tmp");
        for(int i = 0; i < roles.size(); i++)
        {
            try
            {
                coralSession.getSecurity().deleteRole((Role)roles.get(i));
            }
            catch(Exception e)
            {
                coralSession.getSecurity().setName((Role)roles.get(i),"tmp");
            }
        }
    }
    
    public void uninstallOldRelationsips(Subject subject)
        throws Exception
    {
        // remove forum role instances
        deleteResources("/cms/sites/*/relationships");
        deleteResources("/cms/sites/*/media");
        deleteResources("/cms/sites/*/links");
    }

    public void uninstallSearch(Subject subject)
        throws Exception
    
    {
        Resource[] resources = coralSession.getStore().
            getResourceByPath("/cms/sites/*/search");
        for (int i = 0; i < resources.length; i++)
        {
            cmsSecurityService.cleanupRoles(resources[i], true, subject);
        }
        // nuke existing forums
        deleteResources("/cms/sites/*/search");
        deleteResources("/cms/applications/search");

        // delete classes
        deleteResourceClass("search.root");
        deleteResourceClass("search.index");
        deleteResourceClass("search.pool");
        deleteResourceClass("search.external.pool");

        // delete permissions
        deletePermission("cms.search.configure");
        deletePermission("cms.search.index.add");
        deletePermission("cms.search.index.delete");
        deletePermission("cms.search.index.modify");
        deletePermission("cms.search.pool.add");
        deletePermission("cms.search.pool.delete");
        deletePermission("cms.search.pool.modify");
        deletePermission("cms.search.external.pool.add");
        deletePermission("cms.search.external.pool.delete");
        deletePermission("cms.search.external.pool.modify");

    }
    
    public void uninstallForum(Subject subject)
        throws Exception
    {
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
    

    
    public void uninstallBanner(Subject subject)
        throws Exception
    {
        // remove forum role instances
        Resource[] banners = coralSession.getStore().
            getResourceByPath("/cms/sites/*/applications/banners");
        for (int i = 0; i < banners.length; i++)
        {
            cmsSecurityService.cleanupRoles(banners[i], true, subject);
        }
        // nuke existing forums
        deleteResources("/cms/sites/*/applications/banners");
        
        // Delete integration info
        deleteResources("/cms/applications/banner");
        
        // Delete workflow info
        deleteResources("/cms/workflow/automata/banner.banner");

        // delete classes
        deleteResourceClass("cms.banner.banners");
        deleteResourceClass("cms.banner.media_banner");
        deleteResourceClass("cms.banner.external_banner");
        deleteResourceClass("cms.banner.banner");
        deleteResourceClass("cms.banner.pool");
            
        // delete permissions
        deletePermission("cms.banner.banners.administer");
        deletePermission("cms.banner.banners.system");
    }

    public void uninstallPoll(Subject subject)
        throws Exception
    {
        // remove forum role instances
        Resource[] resources = coralSession.getStore().
            getResourceByPath("/cms/sites/*/applications/polls");
        for (int i = 0; i < resources.length; i++)
        {
            cmsSecurityService.cleanupRoles(resources[i], true, subject);
        }
        // nuke existing forums
        deleteResources("/cms/sites/*/applications/polls");
        
        // Delete integration info
        deleteResources("/cms/applications/poll");
        
        // Delete workflow info
        deleteResources("/cms/workflow/automata/poll.poll");

        // delete classes
        deleteResourceClass("cms.poll.polls");
        deleteResourceClass("cms.poll.poll");
        deleteResourceClass("cms.poll.question");
        deleteResourceClass("cms.poll.answer");
        deleteResourceClass("cms.poll.result");
        deleteResourceClass("cms.poll.pool");
            
        // delete permissions
        deletePermission("cms.poll.polls.administer");
        deletePermission("cms.poll.polls.system");
        deletePermission("cms.poll.poll.moderate");
    }

    public void uninstallLink(Subject subject)
        throws Exception
    {
        // remove forum role instances
        Resource[] resources = coralSession.getStore().
            getResourceByPath("/cms/sites/*/applications/links");
        for (int i = 0; i < resources.length; i++)
        {
            cmsSecurityService.cleanupRoles(resources[i], true,  subject);
        }
        // nuke existing forums
        deleteResources("/cms/sites/*/applications/links");
        
        // Delete integration info
        deleteResources("/cms/applications/link");
        
        // Delete workflow info
        deleteResources("/cms/workflow/automata/link.link");

        // delete classes
        deleteResourceClass("cms.link.link_root");
        deleteResourceClass("cms.link.external_link");
        deleteResourceClass("cms.link.cms_link");
        deleteResourceClass("cms.link.base_link");
        deleteResourceClass("cms.link.pool");
            
        // delete permissions
        deletePermission("cms.link.links.administer");
        deletePermission("cms.link.links.system");
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
            try
            {
                coralSession.getStore().deleteTree(res[i]);
            }
            catch(Exception e)
            {
                log.error("Exception occured during resource deleting: ",e);
            }
        }
    }
}
