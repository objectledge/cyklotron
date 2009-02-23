package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.Entity;
import org.objectledge.coral.schema.CircularDependencyException;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.SecurityException;
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
import net.cyklotron.cms.modules.views.security.RoleAssignments;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

public class UpdateRoleAssignments
    extends BaseSecurityAction
{
    private final CoralSessionFactory coralSessionFactory;

    public UpdateRoleAssignments(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager,
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        this.coralSessionFactory = coralSessionFactory;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CoralSession rootSession = coralSessionFactory.getRootSession();
        try
        {
            long roleId = parameters.getLong("role_id");
            RoleResource roleRes = (RoleResource)rootSession.getStore().getResource(roleId);
            Role role = roleRes.getRole();
            Set<Subject> currentSubjects = RoleAssignments.getAssignedSubjects(role);
            Set<Subject> selectedSubjects = RoleAssignments.getSelectedEntities(parameters,
                "subject", rootSession.getSecurity().getSubjectFactory());
            updateGrants(currentSubjects, selectedSubjects, role, rootSession);
            Set<Role> currentGroups = RoleAssignments.getAssignedGroups(role);
            Set<Role> selectedGroups = RoleAssignments.getSelectedEntities(parameters, "group",
                rootSession.getSecurity().getRoleFactory());
            updateGrants(currentGroups, selectedGroups, role, rootSession);
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

    public static <E extends Entity> void updateGrants(Set<E> current, Set<E> selected, Role role,
        CoralSession coralSession)
        throws SecurityException, CircularDependencyException
    {
        Set<E> all = new HashSet<E>(current.size() + selected.size());
        all.addAll(current);
        all.addAll(selected);
        for (E e : all)
        {
            if(!current.contains(e) && selected.contains(e))
            {
                // grant
                if(e instanceof Subject)
                {
                    coralSession.getSecurity().grant(role, (Subject)e, false);
                }
                if(e instanceof Role)
                {
                    coralSession.getSecurity().addSubRole((Role)e, role);
                }
            }
            if(current.contains(e) && !selected.contains(e))
            {
                // revoke
                if(e instanceof Subject)
                {
                    coralSession.getSecurity().revoke(role, (Subject)e);
                }
                if(e instanceof Role)
                {
                    coralSession.getSecurity().deleteSubRole((Role)e, role);
                }
            }
        }
    }
}
