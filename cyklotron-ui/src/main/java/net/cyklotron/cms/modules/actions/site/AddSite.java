package net.cyklotron.cms.modules.actions.site;

import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.authentication.UnknownUserException;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddSite.java,v 1.2 2005-01-24 10:27:51 pablo Exp $
 */
public class AddSite
    extends BaseSiteAction
{
    protected AuthenticationService authenticationService;

    public AddSite()
    {
        authenticationService = (AuthenticationService)
            broker.getService(AuthenticationService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String name = parameters.get("name","");
        String description = parameters.get("description","");

        long templateId = parameters.getLong("template_id", -1);
        String owner = parameters.get("owner",null);
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
        if(templateId == -1)
        {
            templatingContext.put("result","site_template_empty");
        }
        if(name.equals(""))
        {
            templatingContext.put("result","site_name_empty");
        }
        if(dn != null && name.length() > 0 && templateId > 0)
        {
            try
            {
                SiteResource template = SiteResourceImpl.getSiteResource(coralSession,
                                                                         templateId);
                Subject ownerSubject = coralSession.getSecurity().getSubject(dn);
                SiteResource site = ss.createSite(template, name, description,
                                                  ownerSubject, coralSession.getUserSubject());
                parameters.set("site_id", site.getIdString());
            }
            catch(Exception e)
            {
                templatingContext.put("result","exception");
                log.error("AddSite",e);
                templatingContext.put("trace", new StackTrace(e));
            }
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("site,AddSite");
            templatingContext.put("name", name);
            templatingContext.put("description", description);
            templatingContext.put("template_id", new Long(templateId));
            templatingContext.put("owner", owner);
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
