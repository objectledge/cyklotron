package net.cyklotron.cms.modules.views.security;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

public class RoleList
    extends BaseRoleScreen
{
    private static String TABLE_NAME = "cms.security.RoleList";

    public RoleList()
    {
        super();
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            templatingContext.put("roles", getRoleTable(data, site));
            templatingContext.put("path_tool", new PathTool(site));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }

    protected String getTableName()
    {
        return TABLE_NAME;
    }
}
