package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;
import java.util.Iterator;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleAssignment;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

public class UpdateMemberAssignments
    extends BaseSecurityAction
{
    public UpdateMemberAssignments(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        // TODO Auto-generated constructor stub
    }
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long subjectId = parameters.getLong("subject_id");
            Subject subject = coralSession.getSecurity().getSubject(subjectId);
            long[] roleIds = parameters.getLongs("role_id");
            HashSet roles = new HashSet();
            for(int i=0; i<roleIds.length; i++)
            {
                roles.add(coralSession.getSecurity().getRole(roleIds[i]));
            }
            long[] selectedRoleIds = parameters.getLongs("selected_role_id");
            HashSet selectedRoles = new HashSet();
            for(int i=0; i<selectedRoleIds.length; i++)
            {
                selectedRoles.add(coralSession.getSecurity().getRole(selectedRoleIds[i]));
            }
            RoleAssignment[] subjectAssignments = subject.getRoleAssignments();
            HashSet subjectRoles = new HashSet();
            for(int i=0; i<subjectAssignments.length; i++)
            {
                subjectRoles.add(subjectAssignments[i].getRole());
            }
            Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
            Iterator i = roles.iterator();
            while(i.hasNext())
            {
                Role role = (Role)i.next();
                if(selectedRoles.contains(role))
                {
                    if(!subjectRoles.contains(role))
                    {
                        coralSession.getSecurity().grant(role, subject, false);
                    }
                }
                else
                {
                    if(subjectRoles.contains(role))
                    {
                        coralSession.getSecurity().revoke(role, subject);
                    }
                }
            }
            templatingContext.put("result", "updated_successfully");
        }
        catch(Exception e)
        {
            // log.error("AddMember", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
}
