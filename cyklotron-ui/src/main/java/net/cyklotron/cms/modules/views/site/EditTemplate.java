package net.cyklotron.cms.modules.views.site;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;

public class EditTemplate
    extends BaseSiteScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long templateId = parameters.getLong("template");
            SiteResource template = SiteResourceImpl.
                getSiteResource(coralSession, templateId);
            templatingContext.put("template", template);
        }
        catch(Exception e)
        {
            log.error("EditSite",e);
            throw new ProcessingException("failed to lookup site",e);
        }
    }
}
