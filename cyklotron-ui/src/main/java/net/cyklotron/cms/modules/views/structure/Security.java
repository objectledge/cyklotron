package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.Collections;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class Security
    extends BaseStructureScreen
{
    private SecurityService cmsSecurityService;

    public Security()
    {
        super();
        cmsSecurityService = (SecurityService)broker.
            getService(SecurityService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        ArrayList list = new ArrayList();

        NavigationNodeResource node = getNode();

        Resource r = node;
        while(r != null && r instanceof NavigationNodeResource)
        {
            list.add(r);
            r = r.getParent();
        }
        Collections.reverse(list);
        templatingContext.put("nodes", list);
        templatingContext.put("role_tool", new RoleTool(node.getSite()));
    }

    public class RoleTool
    {
        private SiteResource site;

        public RoleTool(SiteResource site)
        {
            this.site = site;
        }

        public RoleResource getRoleResource(Role role)
        {
            if(role != null)
            {
                return cmsSecurityService.getRole(site, role);
            }
            else
            {
                return null;
            }
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkAdministrator(coralSession);
    }
}
