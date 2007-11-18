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
import org.objectledge.coral.session.CoralSessionFactory;
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
    private CoralSessionFactory coralSessionFactory;
    
    public UpdateRoleAssignments(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager,
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        this.coralSessionFactory = coralSessionFactory;
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CoralSession rootSession = coralSessionFactory.getRootSession();
        try
        {
            long roleId = parameters.getLong("role_id");
            RoleResource roleRes = (RoleResource)rootSession.getStore().getResource(roleId);
            Role role = roleRes.getRole();
            long[] subjectIds = parameters.getLongs("subject_id");
            HashSet<Subject> subjects = new HashSet<Subject>();
            for(int i=0; i<subjectIds.length; i++)
            {
                subjects.add(rootSession.getSecurity().getSubject(subjectIds[i]));
            }
            long[] selectedSubjectIds = parameters.getLongs("selected_subject_id");
            HashSet<Subject> selectedSubjects = new HashSet<Subject>();
            for(int i=0; i<selectedSubjectIds.length; i++)
            {
                selectedSubjects.add(rootSession.getSecurity().getSubject(selectedSubjectIds[i]));
            }
            RoleAssignment[] roleAssignments = role.getRoleAssignments();
            HashSet<Subject> roleSubjects = new HashSet<Subject>();
            for(int i=0; i<roleAssignments.length; i++)
            {
                roleSubjects.add(roleAssignments[i].getSubject());
            }
            Iterator i = subjects.iterator();
            while(i.hasNext())
            {
                Subject subject = (Subject)i.next();
                if(selectedSubjects.contains(subject))
                {
                    if(!roleSubjects.contains(subject))
                    {
                        rootSession.getSecurity().grant(role, subject, false);
                    }
                }
                else
                {
                    if(roleSubjects.contains(subject))
                    {
                        rootSession.getSecurity().revoke(role, subject);
                    }
                }
            }
            templatingContext.put("result", "updated_successfully");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        finally
        {
            rootSession.close();
        }
    }
}
