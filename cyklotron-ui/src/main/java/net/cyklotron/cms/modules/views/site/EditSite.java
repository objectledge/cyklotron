package net.cyklotron.cms.modules.views.site;


import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 *
 */
public class EditSite
    extends BaseSiteScreen
{
    protected UserManager userManager;

        
    public EditSite(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, SiteService siteService,
        UserManager userManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, siteService);
        this.userManager = userManager;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();

            templatingContext.put("current", coralSession.getUserSubject());
            if(!templatingContext.containsKey("owner"))
            {
                templatingContext.put("owner", userManager.getLogin(site.getOwner().getName()));
            }
            if(!templatingContext.containsKey("description"))
            {
                templatingContext.put("description", site.getDescription());
            }
            templatingContext.put("br_description", StringUtils.htmlLineBreaks(site.getDescription()));
            templatingContext.put("cms_admin", coralSession.getSecurity().
                        getUniqueRole("cms.administrator"));
        }
        catch(Exception e)
        {
            logger.error("EditSite",e);
            throw new ProcessingException("failed to lookup site",e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role cmsAdmin = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(getSite().getTeamMember()) || 
            coralSession.getUserSubject().hasRole(cmsAdmin);
    }
}
