package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.Collections;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class Security
    extends BaseStructureScreen
{
    private SecurityService cmsSecurityService;



    public Security(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        StructureService structureService, StyleService styleService, SiteService siteService,
        RelatedService relatedService, SecurityService cmsSecurityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        this.cmsSecurityService = cmsSecurityService;
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
                CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
                return cmsSecurityService.getRole(coralSession, site, role);
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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkAdministrator(coralSession);
    }
}
