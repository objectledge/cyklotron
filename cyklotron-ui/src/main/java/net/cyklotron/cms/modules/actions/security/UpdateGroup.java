package net.cyklotron.cms.modules.actions.security;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
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
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

public class UpdateGroup
    extends BaseSecurityAction
{

    public UpdateGroup(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long groupId = parameters.getLong("group_id");
            RoleResource group = RoleResourceImpl.getRoleResource(coralSession, groupId);
            SiteResource site = cmsDataFactory.getCmsData(context).getSite();
            String name = parameters.get("name", "");
            String description = parameters.get("description", "");
            boolean sharingWorkgroup = parameters.getBoolean("sharingWorkgroup", false);
            String result = null;
            if(!name.equals(cmsSecurityService.getShortGroupName(group))
                && cmsSecurityService.isGroupNameInUse(coralSession, site, name))
            {
                result = "group_name_in_use";
            }
            if(!cmsSecurityService.isValidGroupName(name))
            {
                result = "invalid_group_name";
            }
            if(result == null)
            {
                String fullName = cmsSecurityService.getFullGroupName(site, name);
                if(!fullName.equals(group.getName()))
                {
                    coralSession.getStore().setName(group, fullName);
                }
                group.setDescription(description);
                group.setSharingWorkgroup(sharingWorkgroup);
                group.update();
                templatingContext.put("result", "updated_successfully");
            }
            else
            {
                templatingContext.put("result", result);
                mvcContext.setView("security.EditGroup");
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
}
