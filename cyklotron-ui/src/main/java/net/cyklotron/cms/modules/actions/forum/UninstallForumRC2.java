/*
 */
package net.cyklotron.cms.modules.actions.forum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.SecurityService;
import net.labeo.services.database.DatabaseService;
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
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class UninstallForumRC2 
    extends BaseCMSAction
{
	private DatabaseService databaseService;

	protected Logger log;
	
	public UninstallForumRC2()
	{
		log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("fixes");
		databaseService = (DatabaseService)broker.getService(DatabaseService.SERVICE_NAME);	
	}
	
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
            // remove forum role instances
            cleanupSecurity(subject);
            
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


	private void cleanupSecurity(Subject subject)
		throws Exception
	{
		Connection conn = null;
		try
		{
			conn = databaseService.getConnection();
			cleanupSecuritySchema(conn,"cms.forum.visitor");
			cleanupSecuritySchema(conn,"cms.forum.participant");
			cleanupSecuritySchema(conn,"cms.forum.moderator");
			cleanupSecuritySchema(conn,"cms.forum.administrator");
			cleanupRoles(subject);
		}
		catch (Exception e)
		{
			System.out.println("Exception occured: " + StringUtils.stackTrace(e));
			return;
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
			}
			catch (SQLException e)
			{
				log.error("failed to close connection", e);
			}
		}
	}

	private void cleanupSecuritySchema(Connection conn, String name)
		throws Exception
	{
		ArrayList nodes = new ArrayList();
		Statement statement = conn.createStatement();
		ResultSet rs =
		statement.executeQuery(
			"SELECT * FROM arl_resource WHERE name like '"+name+".%'");
		while (rs.next())
		{
			nodes.add(new Long(rs.getLong("resource_id")));
		}
	    System.out.println("Query executed");
		for (int i = 0; i < nodes.size(); i++)
		{
			Long nodeId = (Long)nodes.get(i);
			try
			{
				Resource resource = coralSession.getStore().getResource(nodeId.longValue());
				System.out.println("Kasuje zasob i jego dzieci nr. "+nodeId+" : "+resource.getPath());
				coralSession.getStore().deleteTree(resource);
			}
			catch(EntityDoesNotExistException e)
			{
				// already removed...
			}
		}
	}

	private void cleanupRoles(Subject subject)
		throws Exception
	{
		Role[] roles = coralSession.getSecurity().getRole();
		for(int j = 0; j < roles.length; j++)
		{
			if(roles[j].getName().startsWith("cms.forum."))
			{
				System.out.println("Czyszcze role: "+roles[j].getName());
				PermissionAssignment[] pa = roles[j].getPermissionAssignments();
				for(int i = 0; i < pa.length; i++)
				{
					coralSession.getSecurity().revoke(pa[i].getResource(), pa[i].getRole(), pa[i].getPermission(), subject);
				}
				System.out.println("Usunalem permissiony");
				RoleAssignment[] ra = roles[j].getRoleAssignments();
				for(int i = 0; i < ra.length; i++)
				{
					coralSession.getSecurity().revoke(ra[i].getRole(), ra[i].getSubject(), subject);
				}
				System.out.println("Usunalem granty");
				RoleImplication[] ri = roles[j].getImplications();
				for(int i = 0; i < ri.length; i++)
				{
					coralSession.getSecurity().deleteSubRole(ri[i].getSuperRole(), ri[i].getSubRole());
				}
				System.out.println("Usunalem zaleznosci rol");
				coralSession.getSecurity().deleteRole(roles[j]);
				System.out.println("Usunalem role");
			}
		}
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
