package net.cyklotron.cms.modules.actions.fixes;

import net.labeo.services.resource.CircularDependencyException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * For integracje use only.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FixCategoryRoles.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class FixCategoryRoles extends BaseCMSAction
{
    private SecurityService cmsSecurityService;

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        SiteService siteService = (SiteService) (broker.getService(SiteService.SERVICE_NAME));
        cmsSecurityService = (SecurityService) (broker.getService(SecurityService.SERVICE_NAME));
        SiteResource[] sites = siteService.getSites();
        for (int i = 0; i < sites.length; i++)
        {
            fixSite(sites[i], subject);
        }
    }

    private void fixSite(SiteResource site, Subject subject) throws ProcessingException
    {
        Role[] roles = coralSession.getSecurity().getRole("cms.category.administrator." + site.getName());
        Role categoryRole = null;
        if (roles.length > 0)
        {
            categoryRole = roles[0];
        }
        else
        {
            categoryRole = coralSession.getSecurity().createRole("cms.category.administrator." + site.getName());
        }
        roles = coralSession.getSecurity().getRole("cms.category.query.administrator." + site.getName());
        Role categoryQueryRole = null;
        if (roles.length > 0)
        {
            categoryQueryRole = roles[0];
        }
        else
        {
            categoryQueryRole = coralSession.getSecurity().createRole("cms.category.query.administrator." + site.getName());
        }

        roles = coralSession.getSecurity().getRole("cms.site.administrator." + site.getName());
        Role siteRole = null;
        if (roles.length > 0)
        {
            siteRole = roles[0];
        }
        else
        {
            throw new ProcessingException("Site has no administrator role!!!");
        }

        grant(categoryRole, site, "cms.category.add", subject);
        grant(categoryRole, site, "cms.category.delete", subject);
        grant(categoryRole, site, "cms.category.modify", subject);
        grant(categoryRole, site, "cms.category.move", subject);
        grant(categoryQueryRole, site, "cms.category.query.add", subject);
        grant(categoryQueryRole, site, "cms.category.query.delete", subject);
        grant(categoryQueryRole, site, "cms.category.query.modify", subject);
        grant(categoryQueryRole, site, "cms.category.query.pool.add", subject);
        grant(categoryQueryRole, site, "cms.category.query.pool.delete", subject);
        grant(categoryQueryRole, site, "cms.category.query.pool.modify", subject);

        try
        {
            if (!categoryRole.isSuperRole(siteRole))
            {
                coralSession.getSecurity().addSubRole(siteRole, categoryRole);
            }
            if (!categoryQueryRole.isSuperRole(categoryRole))
            {
                coralSession.getSecurity().addSubRole(categoryRole, categoryQueryRole);
            }
        }
        catch (CircularDependencyException e)
        {
            throw new ProcessingException("circular error ", e);
        }

        Resource secRoot = cmsSecurityService.getRoleInformationRoot(site);
        Resource adminRoot = coralSession.getStore().getUniqueResource(secRoot, "cms.site.administrator." + site.getName());
        Resource[] resources = coralSession.getStore().getResource(adminRoot, "cms.category.administrator." + site.getName());
        Resource catAdmin = null;
        Resource catQAdmin = null;

        try
        {
            if (resources.length > 0)
            {
                catAdmin = resources[0];
            }
            else
            {
                catAdmin =
                    RoleResourceImpl.createRoleResource(
                        coralSession,
                        "cms.category.administrator." + site.getName(),
                        adminRoot,
                        categoryRole,
                        false,
                        subject);
            }
            resources = coralSession.getStore().getResource(catAdmin, "cms.category.query.administrator." + site.getName());
            if (resources.length > 0)
            {
                catQAdmin = resources[0];
            }
            else
            {
                catQAdmin =
                    RoleResourceImpl.createRoleResource(
                        coralSession,
                        "cms.category.query.administrator." + site.getName(),
                        catAdmin,
                        categoryQueryRole,
                        false,
                        subject);
            }
        }
        catch(ValueRequiredException e)
        {
            throw new ProcessingException("value required", e);
        }
    }

    private void grant(Role role, Resource resource, String permission, Subject subject)
    {
        try
        {
            Permission perm = coralSession.getSecurity().getUniquePermission(permission);
            coralSession.getSecurity().grant(resource, role, perm, true, subject);
        }
        catch (Exception exception)
        {
            //rien 
        }
    }
}
