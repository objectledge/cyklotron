package net.cyklotron.cms.modules.views.security;

import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleImplication;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

public class GroupRoleAssignments
    extends BaseRoleScreen
{
    private static String TABLE_NAME = "cms.security.MemberAssignments";



    public GroupRoleAssignments(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            templatingContext.put("roles", getRoleTable(coralSession, site, i18nContext));
            templatingContext.put("path_tool", new PathTool(site));
            long groupId = parameters.getLong("group_id");
            RoleResource group = RoleResourceImpl.getRoleResource(coralSession, groupId);
            templatingContext.put("group", group);
            templatingContext.put("groupName", cmsSecurityService.getShortGroupName(group));
            templatingContext.put("assigned", getAssignedRoles(coralSession, group, site));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }

    public Set<Role> getAssignedRoles(CoralSession coralSession, RoleResource group, SiteResource site)
    {
        Set<Role> assignedRoles = new HashSet<Role>();
        Set<Role> siteRoles = new HashSet<Role>();
        RoleResource[] rrs = cmsSecurityService.getRoles(coralSession, site);
        for(RoleResource rr : rrs)
        {
            if(!cmsSecurityService.isGroupResource(rr))
            {
                siteRoles.add(rr.getRole());
            }
        }
        RoleImplication[] ris = group.getRole().getImplications();
        for(RoleImplication ri : ris)
        {
            if(ri.getSuperRole().equals(group.getRole()) && siteRoles.contains(ri.getSubRole()))
            {
                assignedRoles.add(ri.getSubRole());
            }
        }
        
        return assignedRoles;
    }

    protected String getTableName()
    {
        return TABLE_NAME;
    }
}
