package net.cyklotron.cms.modules.views.structure;

import java.util.Arrays;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 *
 */
public class AddNode
    extends BaseStructureScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite();
        try
        {
            templatingContext.put("styles", Arrays.asList(styleService.getStyles(site)));
        }
        catch(Exception e)
        {
            throw new ProcessingException("falied to list layouts for site "+site.getName(), e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return getCmsData().getNode().canAddChild(coralSession.getUserSubject());
    }
}
