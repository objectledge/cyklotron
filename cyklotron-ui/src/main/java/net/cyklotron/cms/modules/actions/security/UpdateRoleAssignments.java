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
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

public class UpdateRoleAssignments
    extends BaseSecurityAction
{
    public UpdateRoleAssignments(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        
    }
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long roleId = parameters.getLong("role_id");
            RoleResource roleRes = (RoleResource)coralSession.getStore().getResource(roleId);
            Role role = roleRes.getRole();
            long[] subjectIds = parameters.getLongs("subject_id");
            HashSet subjects = new HashSet();
            for(int i=0; i<subjectIds.length; i++)
            {
                subjects.add(coralSession.getSecurity().getSubject(subjectIds[i]));
            }
            long[] selectedSubjectIds = parameters.getLongs("selected_subject_id");
            HashSet selectedSubjects = new HashSet();
            for(int i=0; i<selectedSubjectIds.length; i++)
            {
                selectedSubjects.add(coralSession.getSecurity().getSubject(selectedSubjectIds[i]));
            }
            RoleAssignment[] roleAssignments = role.getRoleAssignments();
            HashSet roleSubjects = new HashSet();
            for(int i=0; i<roleAssignments.length; i++)
            {
                roleSubjects.add(roleAssignments[i].getSubject());
            }
            Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
            Iterator i = subjects.iterator();
            while(i.hasNext())
            {
                Subject subject = (Subject)i.next();
                if(selectedSubjects.contains(subject))
                {
                    if(!roleSubjects.contains(subject))
                    {
                        coralSession.getSecurity().grant(role, subject, false);
                    }
                }
                else
                {
                    if(roleSubjects.contains(subject))
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
