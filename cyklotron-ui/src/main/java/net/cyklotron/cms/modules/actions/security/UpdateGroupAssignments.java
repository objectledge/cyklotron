package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleImplication;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

public class UpdateGroupAssignments
    extends BaseSecurityAction
{
	private CoralSessionFactory sessionFactory;
	
	public UpdateGroupAssignments(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager,
        CoralSessionFactory sessionFactory)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
		this.sessionFactory = sessionFactory;
        
    }
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long groupId = parameters.getLong("group_id");
            RoleResource group = RoleResourceImpl.getRoleResource(coralSession, groupId);
            
            long[] roleIds = parameters.getLongs("role_id");
            Set<Role> roles = new HashSet<Role>();
            for(int i=0; i<roleIds.length; i++)
            {
                roles.add(coralSession.getSecurity().getRole(roleIds[i]));
            }
            long[] selectedRoleIds = parameters.getLongs("selected_role_id");
            Set<Role> selectedRoles = new HashSet<Role>();
            for(int i=0; i<selectedRoleIds.length; i++)
            {
                selectedRoles.add(coralSession.getSecurity().getRole(selectedRoleIds[i]));
            }
            RoleImplication[] roleImplications = group.getRole().getImplications();
            Set<Role> currentRoles = new HashSet<Role>();
            for(RoleImplication ri : roleImplications)
            {
                if(ri.getSuperRole().equals(group.getRole()))
                {
                    currentRoles.add(ri.getSubRole());
                }
            }
			
			CoralSession rootCoralSession = sessionFactory.getRootSession();
			try
			{
			    for(Role role : roles)
		        {
		            if(selectedRoles.contains(role))
		            {
		                if(!currentRoles.contains(role))
		                {
		                    rootCoralSession.getSecurity().addSubRole(group.getRole(), role);
		                }
		            }
		            else
		            {
		                if(currentRoles.contains(role))
		                {
		                    rootCoralSession.getSecurity().deleteSubRole(group.getRole(), role);
		                }
		            }
		        }
			}
			finally
			{
				rootCoralSession.close();
			}
            templatingContext.put("result", "updated_successfully");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
}
