package net.cyklotron.cms.modules.views.site;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;

public class EditTemplate
    extends BaseSiteScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
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
