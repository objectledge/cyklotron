package net.cyklotron.cms.modules.actions.security;

import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
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

public class UpdateSpecialGroupAssignments
    extends BaseSecurityAction
{
    private final CoralSessionFactory coralSessionFactory;

    public UpdateSpecialGroupAssignments(Logger logger, StructureService structureService,
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
            long groupId = parameters.getLong("group_id");
            RoleResource groupRes = (RoleResource)rootSession.getStore().getResource(groupId);
            Role groupRole = groupRes.getRole();
            Set<Role> currentSpecialGroups = RoleAssignments.getAssignedGroups(groupRole);
            Set<Role> selectedSpecialGroups = RoleAssignments.getSelectedEntities(parameters, "special_group",
                rootSession.getSecurity().getRoleFactory());            
            UpdateRoleAssignments.updateGrants(currentSpecialGroups, selectedSpecialGroups, groupRole, rootSession);
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
