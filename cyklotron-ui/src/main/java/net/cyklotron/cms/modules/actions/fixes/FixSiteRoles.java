package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.PermissionAssignment;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * 
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: FixSiteRoles.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class FixSiteRoles
    extends BaseCMSAction
{
    /** site service */
    private SiteService siteService;
    
    /** resource service */
    private SecurityService cmsSecurityService;

    protected Logger log;

    private Role masterAdmin;

    private Subject root;
    
    private Role workgroup;
    
    private Permission siteAdminister;
    
    private Permission layoutAdminister;

    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        try
        {
            siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
            cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);
            log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("fixes");
            SiteResource[] sites = siteService.getSites();
            masterAdmin = coralSession.getSecurity().getUniqueRole("cms.administrator");
            root = coralSession.getSecurity().getSubject(Subject.ROOT);
            workgroup = coralSession.getSecurity().getUniqueRole("cms.workgroup");
            siteAdminister = coralSession.getSecurity().
                getUniquePermission("cms.site.administer");
            layoutAdminister = coralSession.getSecurity().
                getUniquePermission("cms.layout.administer");

            for(int i = 0; i < sites.length; i++)
            {
                fixSite(sites[i], subject, data);
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }

    public void fixSite(SiteResource site, Subject subject, RunData data)
        throws Exception
    {
        SecurityService cmsSecurityService = (SecurityService)broker.
            getService(SecurityService.SERVICE_NAME);
        String name = site.getName();
        Subject owner = site.getOwner();
        try
        {
            Role teamMember = site.getTeamMember();
            Role administrator = site.getAdministrator();
            Role layoutAdministrator = site.getLayoutAdministrator();
            Role siteRole = site.getSiteRole();
            
            if(teamMember == null)
            {
                try
                {
                    teamMember = coralSession.getSecurity().
                        getUniqueRole("cms.site.team_member."+name);
                }
                catch(Exception e)
                { 
                    teamMember = coralSession.getSecurity().
                        createRole("cms.site.team_member."+name);
                }
                site.setTeamMember(teamMember);
            }
            if(administrator == null)
            {
                try
                {
                    administrator = coralSession.getSecurity().
                        getUniqueRole("cms.site.administrator."+name);
                }
                catch(Exception e)
                { 
                    administrator = coralSession.getSecurity().
                        createRole("cms.site.administrator."+name);
                }
                site.setAdministrator(administrator);
            }
            if(layoutAdministrator == null)
            {
                try
                {
                     layoutAdministrator = coralSession.getSecurity().
                         getUniqueRole("cms.layout.administrator."+name);
                }
                catch(Exception e)
                { 
                    layoutAdministrator = coralSession.getSecurity().
                        createRole("cms.layout.administrator."+name);
                }
                site.setLayoutAdministrator(layoutAdministrator);
            }
            if(siteRole == null)
            {
                try
                {
                    siteRole = coralSession.getSecurity().
                        getUniqueRole("cms.site.siterole."+name);
                }
                catch(Exception e)
                { 
                    siteRole = coralSession.getSecurity().
                      createRole("cms.site.siterole."+name);
                }
                site.setSiteRole(siteRole);
            }
            site.update(owner);
            
            coralSession.getSecurity().addSubRole(masterAdmin, administrator);
            coralSession.getSecurity().addSubRole(administrator, layoutAdministrator);
            coralSession.getSecurity().addSubRole(workgroup, teamMember);  
            
            coralSession.getSecurity().
                grant(teamMember, owner, true, root);
            coralSession.getSecurity().
                grant(administrator, owner, true, root);
  
            cmsSecurityService.
                registerRole(site, teamMember, null, false,
                             false, "cms.site.team_member", null, owner);

            RoleResource administratorRole = cmsSecurityService.
                registerRole(site, administrator, null, false, false,
                            "cms.site.administrator", null, owner);
            cmsSecurityService.
                registerRole(site, layoutAdministrator, null, false, false,
                             "cms.layout.administrator", administratorRole, owner);


            // be sure admin has administer permission on site
            PermissionAssignment[] pa = administrator.getPermissionAssignments(site);
            boolean grant = true;
            for(int i = 0; i < pa.length; i++)
            {
                if(pa[i].getPermission().equals(siteAdminister))
                {
                    grant = false;
                }
            }
            if(grant)
            {
                coralSession.getSecurity().
                    grant(site, administrator, siteAdminister, true, root);
            }
            
            // be sure layout admin has permission on site
            pa = layoutAdministrator.getPermissionAssignments(site);
            grant = true;
            for(int i = 0; i < pa.length; i++)
            {
                if(pa[i].getPermission().equals(layoutAdminister))
                {
                    grant = false;
                }
            }
            if(grant)
            {
                coralSession.getSecurity().
                    grant(site, layoutAdministrator, layoutAdminister, true, root);
            }            
            log.debug("FIX ACTION COMPLETED FOR SITE "+site.getName());
        }
        catch(Exception e)
        {
            log.error("failed to setup security for site "+name, e);
        }
    }
}
