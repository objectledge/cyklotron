package net.cyklotron.cms.modules.views.appearance.style;

import java.util.Arrays;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;

/**
 *
 */
public class DeleteStyle
    extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long styleId = parameters.getLong("style_id", -1);
        if(styleId == -1)
        {
            throw new ProcessingException("style id couldn't be found");
        }
        try 
        {
            StyleResource resource =  StyleResourceImpl.getStyleResource(coralSession, styleId);
            Resource[] children = coralSession.getStore().getResource(resource);
            templatingContext.put("style",resource);
            templatingContext.put("referers", styleService.getReferringNodes(resource));
            templatingContext.put("children", Arrays.asList(children));
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to load information", e);
        }
    }
}
