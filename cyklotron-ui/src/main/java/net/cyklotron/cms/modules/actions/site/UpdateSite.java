package net.cyklotron.cms.modules.actions.site;

import net.labeo.Labeo;
import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.authentication.UnknownUserException;
import net.labeo.services.resource.RoleAssignment;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateSite.java,v 1.1 2005-01-24 04:35:11 pablo Exp $
 */
public class UpdateSite
    extends BaseSiteAction
{
    protected AuthenticationService authenticationService;

    public UpdateSite()
    {
        authenticationService = (AuthenticationService)Labeo.getBroker().
            getService(AuthenticationService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        String description = parameters.get("description","");
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
                dn = authenticationService.getUserByLogin(owner).getName();
            }
            catch(UnknownUserException e)
            {
                templatingContext.put("result","owner_name_invalid");
            }
        }
        if(dn != null)
        {
            try
            {
                SiteResource site = getSite(context);
                if(!site.getDescription().equals(description))
                {
                    site.setDescription(description);
                    site.update(subject);
                }
                Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
                Subject newOwner = coralSession.getSecurity().getSubject(dn);
                Subject oldOwner = site.getOwner();
                if(!oldOwner.equals(newOwner))
                {
                    if(!newOwner.hasRole(site.getTeamMember()))
                    {
                        coralSession.getSecurity().
                            grant(site.getTeamMember(), newOwner, true, root);
                    }
                    coralSession.getSecurity().
                        grant(site.getAdministrator(), newOwner, false, root);
                    
                    RoleAssignment[] roleAssignments = site.getAdministrator().getRoleAssignments();
                    for(int i = 0; i < roleAssignments.length; i++)
                    {
                        if(roleAssignments[i].getSubject().equals(oldOwner))
                        {
                            coralSession.getSecurity().
                                revoke(site.getAdministrator(), oldOwner, root);
                        }
                    }
                    coralSession.getStore().setOwner(site, newOwner);
                }
            }
            catch(Exception e)
            {
                templatingContext.put("result","exception");
                log.error("AddSite",e);
                templatingContext.put("trace", StringUtils.stackTrace(e));
            }
        }
        if(context.containsKey("result"))
        {
            data.setView("site,EditSite");
            templatingContext.put("owner", owner);
            templatingContext.put("description", description);
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return coralSession.getUserSubject().hasRole(getSite(context).getAdministrator());
    }
}
