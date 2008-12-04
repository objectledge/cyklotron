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
import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

public class AddGroup
    extends BaseSecurityAction
{

    public AddGroup(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = cmsDataFactory.getCmsData(context).getSite();
        String name = parameters.get("name");
        String description = parameters.get("description");
        String result = null;
        if(cmsSecurityService.isGroupNameInUse(coralSession, site, name))
        {
            result = "group_name_in_use";
        }
        if(!cmsSecurityService.isValidGroupName(name))
        {
            result = "invalid_group_name";
        }
        if(result == null)
        {
            try
            {
                RoleResource group = cmsSecurityService.createGroup(coralSession, site, name);
                group.setDescription(description);
                group.update();
                templatingContext.put("result", "group_added");
            }
            catch(CmsSecurityException e)
            {
                templatingContext.put("result", "exception");
                templatingContext.put("trace", new StackTrace(e));
            }
        }
        else
        {
            templatingContext.put("result", result);
            mvcContext.setView("security.AddGroup");
            templatingContext.put("name", name);
            templatingContext.put("description", description);
        }
    }
}
