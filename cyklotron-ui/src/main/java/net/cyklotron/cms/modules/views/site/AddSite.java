package net.cyklotron.cms.modules.views.site;

import java.util.Arrays;

import net.labeo.services.resource.Role;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;

/**
 *
 */
public class AddSite
    extends BaseSiteScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        templatingContext.put("templates",Arrays.asList(siteService.getTemplates()));
        CmsData cmsData = getCmsData();
        try
        {
            templatingContext.put("login", cmsData.getUserData().getLogin());
        }
        catch(Exception e)
        {
            throw new ProcessingException("can't get user's login", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
