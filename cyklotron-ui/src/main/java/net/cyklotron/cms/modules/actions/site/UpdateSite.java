package net.cyklotron.cms.modules.actions.site;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.RoleAssignment;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateSite.java,v 1.6 2007-02-25 15:53:39 rafal Exp $
 */
public class UpdateSite
    extends BaseSiteAction
{
    protected UserManager userManager;
    private final IntegrationService integrationService;
    
    public UpdateSite(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        SiteService siteService, UserManager userManager, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, siteService);
        this.userManager = userManager;
        this.integrationService = integrationService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        Subject subject = coralSession.getUserSubject();
        String description = parameters.get("description","");
        boolean requiresSecureChannel = parameters.getBoolean("requiresSSL", false);
        String owner = parameters.get("owner","");
                String dn = null;
        if(owner == null || owner.length() == 0)
        {
            templatingContext.put("result","owner_name_empty");
        }
        else
        {
            try
            {
                dn = userManager.getUserByLogin(owner).getName();
            }
            catch(Exception e)
            {
                templatingContext.put("result","owner_name_invalid");
            }
        }
        try
        {
            SiteResource site = getSite(context);
            if(dn != null)
            {
                site.setDescription(description);
                site.setRequiresSecureChannel(requiresSecureChannel);
                site.update();
                Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
                Subject newOwner = coralSession.getSecurity().getSubject(dn);
                Subject oldOwner = site.getOwner();
                if(!oldOwner.equals(newOwner))
                {
                    if(!newOwner.hasRole(site.getTeamMember()))
                    {
                        coralSession.getSecurity().
                            grant(site.getTeamMember(), newOwner, true);
                    }
                    coralSession.getSecurity().
                        grant(site.getAdministrator(), newOwner, false);
                    
                    RoleAssignment[] roleAssignments = site.getAdministrator().getRoleAssignments();
                    for(int i = 0; i < roleAssignments.length; i++)
                    {
                        if(roleAssignments[i].getSubject().equals(oldOwner))
                        {
                            coralSession.getSecurity().
                                revoke(site.getAdministrator(), oldOwner);
                        }
                    }
                    coralSession.getStore().setOwner(site, newOwner);                    
                }
            }
            ApplicationResource[] allApps = integrationService.getApplications(coralSession);
            Set<ApplicationResource> oldApps = new HashSet<ApplicationResource>(
                (asList(integrationService.getEnabledApplications(coralSession, site))));
            String[] newNames = parameters.getStrings("app");
            Set<ApplicationResource> newApps = new HashSet<ApplicationResource>(
                newNames.length);
            for(String appName : newNames) 
            {
                newApps.add(integrationService.getApplication(coralSession, appName));
            }
            for(ApplicationResource app : allApps)
            {
                if(!app.getRequired())
                {
                    if(oldApps.contains(app) && !newApps.contains(app))
                    {
                        integrationService.setApplicationEnabled(coralSession, site, app,
                            false);
                    }
                    else if(!oldApps.contains(app) && newApps.contains(app))
                    {
                        integrationService.setApplicationEnabled(coralSession, site, app,
                            true);
                    }
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            logger.error("AddSite",e);
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("site.EditSite");
            templatingContext.put("owner", owner);
            templatingContext.put("description", description);
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(getSite(context).getAdministrator());
    }
}
