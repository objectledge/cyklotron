package net.cyklotron.cms.modules.actions.fixes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
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
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class FixSecurityServiceRC2 extends BaseCMSAction
{
    private SecurityService cmsSecurityService = null;

    protected Logger log;

    private ArrayList roles;

    private DatabaseService databaseService;

	private Permission viewPermission;
	private Permission addPermission;
	private Permission deletePermission;
	private Permission movePermission;
	private Permission modify_ownPermission;
	private Permission modifyPermission;
	private Permission submitPermission;
	private Permission categorizePermission;

	public FixSecurityServiceRC2()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("fixes");
        cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);
        databaseService = (DatabaseService)broker.getService(DatabaseService.SERVICE_NAME);
        roles = new ArrayList();
    }

    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        System.out.println("Security fix fired");
        try
        {
            Subject subject = coralSession.getUserSubject();
			viewPermission = coralSession.getSecurity().getUniquePermission("cms.structure.view");
			addPermission = coralSession.getSecurity().getUniquePermission("cms.structure.add");
			deletePermission = coralSession.getSecurity().getUniquePermission("cms.structure.delete");
			movePermission = coralSession.getSecurity().getUniquePermission("cms.structure.move");
			modify_ownPermission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_own");
			modifyPermission = coralSession.getSecurity().getUniquePermission("cms.structure.modify");
			submitPermission = coralSession.getSecurity().getUniquePermission("cms.structure.submit");
			categorizePermission = coralSession.getSecurity().getUniquePermission("cms.category.categorize");
            clearSecurity(subject);
        }
        catch (Exception e)
        {
            log.error("Error on fix: try again!!!", e);
			System.out.println("Security fix EXCEPTION!!! "+StringUtils.stackTrace(e));
        }
    }

    public void clearSecurity(Subject subject) throws Exception
    {
        //QueryResults results = coralSession.getQuery().executeQuery("FIND RESOURCE FROM structure.navigation_node");
        //Resource[] resources = coralSession.getStore().getResource();

        ResourceClass nodeClass = coralSession.getSchema().getResourceClass("structure.navigation_node");
        ResourceClass docClass = coralSession.getSchema().getResourceClass("documents.document_node");
        ArrayList nodes = new ArrayList();
        Connection conn = null;
        int counter = 0;
        try
        {
            conn = databaseService.getConnection();
            Statement statement = conn.createStatement();
            ResultSet rs =
                statement.executeQuery(
                    "SELECT * FROM arl_resource WHERE resource_class_id = " + nodeClass.getIdString() + " OR resource_class_id = " + docClass.getIdString());
            while (rs.next())
            {
                nodes.add(new Long(rs.getLong("resource_id")));
            }
            counter = nodes.size();
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
        System.out.println("Query executed");

        for (int i = 0; i < nodes.size(); i++)
        {
            Long nodeId = (Long)nodes.get(i);
            fixNode(NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId.longValue()), subject, i, nodes.size());
        }

    }

    private void fixNode(NavigationNodeResource node, Subject subject, int number, int max) throws Exception
    {
        System.out.println("Processing " + number + " of " + max + "; node " + node.getIdString() + " : " + node.getPath());
		//moderator
        String roleName = "cms.structure.moderator." + node.getIdString();
        Resource[] resources = coralSession.getStore().getResource(roleName);
        if (resources.length > 0)
        {
            coralSession.getStore().deleteResource(resources[0]);
            System.out.println("Moderator exterminated");
        }
        clearRole(roleName, subject);
        deleteRole(roleName, subject);
        
        //reporter
        roleName = "cms.structure.reporter."+node.getIdString();
		resources = coralSession.getStore().getResource(roleName);
		if (resources.length > 0)
		{
			coralSession.getStore().deleteResource(resources[0]);
			System.out.println("Reporter exterminated");
		}
		clearRole(roleName, subject);
		deleteRole(roleName, subject);
        
		//		redactor
		roleName = "cms.structure.redactor."+node.getIdString();
		resources = coralSession.getStore().getResource(roleName);
		if (resources.length > 0)
		{
			coralSession.getStore().deleteResource(resources[0]);
			System.out.println("Reporter exterminated");
		}
		clearRole(roleName, subject);
		deleteRole(roleName, subject);
        
        
		roleName = "cms.structure.administrator."+node.getIdString();
		resources = coralSession.getStore().getResource(roleName);
		if (resources.length > 0)
		{
			clearRole(roleName, subject);
			Role role = coralSession.getSecurity().getUniqueRole(roleName);
			coralSession.getSecurity().grant(node, role, addPermission, true, subject);
			coralSession.getSecurity().grant(node, role, viewPermission, true, subject);
			coralSession.getSecurity().grant(node, role, deletePermission, true, subject);
			coralSession.getSecurity().grant(node, role, movePermission, true, subject);
			coralSession.getSecurity().grant(node, role, modify_ownPermission, true, subject);
			coralSession.getSecurity().grant(node, role, modifyPermission, true, subject);
			coralSession.getSecurity().grant(node, role, submitPermission, true, subject);
			coralSession.getSecurity().grant(node, role, categorizePermission, true, subject);
		}
		
		roleName = "cms.structure.editor."+node.getIdString();
		resources = coralSession.getStore().getResource(roleName);
		if (resources.length > 0)
		{
			clearRole(roleName, subject);
			Role role = coralSession.getSecurity().getUniqueRole(roleName);
			coralSession.getSecurity().grant(node, role, addPermission, true, subject);
			coralSession.getSecurity().grant(node, role, viewPermission, true, subject);
		    coralSession.getSecurity().grant(node, role, movePermission, true, subject);
			coralSession.getSecurity().grant(node, role, modify_ownPermission, true, subject);
			coralSession.getSecurity().grant(node, role, modifyPermission, true, subject);
			coralSession.getSecurity().grant(node, role, submitPermission, true, subject);
			coralSession.getSecurity().grant(node, role, categorizePermission, true, subject);
		}
    }

    /* 
     * (overriden)
     */
    public boolean checkAccess(RunData data) throws ProcessingException
    {
        return checkAdministrator(context, coralSession);
    }

    private void deleteResourceClass(String name) throws Exception
    {
        ResourceClass rc;
        try
        {
            rc = coralSession.getSchema().getResourceClass(name);
        }
        catch (EntityDoesNotExistException e)
        {
            return;
        }
        coralSession.getSchema().deleteResourceClass(rc);
    }

    private void deletePermission(String name) throws Exception
    {
        Permission[] p = coralSession.getSecurity().getPermission(name);
        for (int i = 0; i < p.length; i++)
        {
            coralSession.getSecurity().deletePermission(p[i]);
        }
    }
    
    private void clearRole(String name, Subject subject) throws Exception
    {
		Role[] p = coralSession.getSecurity().getRole(name);
		for (int i = 0; i < p.length; i++)
		{
			PermissionAssignment[] pa = p[i].getPermissionAssignments();
			for(int j = 0; j < pa.length; j++)
			{
				coralSession.getSecurity().revoke(pa[j].getResource(), pa[j].getRole(), pa[j].getPermission(), subject);
			}
		}
    }
    

    private void deleteRole(String name, Subject subject) throws Exception
    {
        Role[] p = coralSession.getSecurity().getRole(name);
        for (int i = 0; i < p.length; i++)
        {
        	RoleAssignment[] ra = p[i].getRoleAssignments();
			for(int j = 0; j < ra.length; j++)
			{
				coralSession.getSecurity().revoke(ra[j].getRole(), ra[j].getSubject(), subject);
			}
			RoleImplication[] ri = p[i].getImplications();
			for(int j = 0; j < ri.length; j++)
			{
				coralSession.getSecurity().deleteSubRole(ri[j].getSuperRole(), ri[j].getSubRole());
			}
			coralSession.getSecurity().deleteRole(p[i]);
        }
    }

    private void deleteResources(String path) throws Exception
    {
        Resource[] res = coralSession.getStore().getResourceByPath(path);
        for (int i = 0; i < res.length; i++)
        {
            try
            {
                coralSession.getStore().deleteTree(res[i]);
            }
            catch (Exception e)
            {
                log.error("Exception occured during resource deleting: ", e);
            }
        }
    }
}
