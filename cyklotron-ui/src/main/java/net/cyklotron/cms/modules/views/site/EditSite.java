package net.cyklotron.cms.modules.views.site;

import net.labeo.Labeo;
import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.resource.Role;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 *
 */
public class EditSite
    extends BaseSiteScreen
{
    protected AuthenticationService authenticationService;

    protected Role cmsAdmin;

    public EditSite()
    {
        authenticationService = (AuthenticationService)Labeo.getBroker().
            getService(AuthenticationService.SERVICE_NAME);
        cmsAdmin = coralSession.getSecurity().getUniqueRole("cms.administrator");
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();

            templatingContext.put("current", coralSession.getUserSubject());
            if(!context.containsKey("owner"))
            {
                templatingContext.put("owner", authenticationService.getLogin(site.getOwner().getName()));
            }
            if(!context.containsKey("description"))
            {
                templatingContext.put("description", site.getDescription());
            }
            templatingContext.put("br_description", StringUtils.htmlLineBreaks(site.getDescription()));
            templatingContext.put("cms_admin", coralSession.getSecurity().
                        getUniqueRole("cms.administrator"));
        }
        catch(Exception e)
        {
            log.error("EditSite",e);
            throw new ProcessingException("failed to lookup site",e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return coralSession.getUserSubject().hasRole(getSite().getTeamMember()) || 
            coralSession.getUserSubject().hasRole(cmsAdmin);
    }
}
