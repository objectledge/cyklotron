package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;

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
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

public class DismissMember
    extends BaseSecurityAction
{
    public DismissMember(Logger logger, StructureService structureService,
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
            SiteResource site = getSite(context);
            RoleResource[] siteRoles = cmsSecurityService.getRoles(coralSession, site);
            Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
            HashSet roles = new HashSet();
            RoleAssignment[] assignments = subject.getRoleAssignments();
            for(int i=0; i<assignments.length; i++)
            {
                roles.add(assignments[i].getRole());
            }
            for(int i=0; i<siteRoles.length; i++)
            {
                Role role = siteRoles[i].getRole();
                if(roles.contains(role))
                {
                    coralSession.getSecurity().revoke(role, subject);
                }
            }
            templatingContext.put("result", "deleted_successfully");
        }
        catch(Exception e)
        {
            // log.error("AddMember", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
}

