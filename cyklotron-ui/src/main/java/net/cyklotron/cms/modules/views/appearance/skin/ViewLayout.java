package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.templating.Template;
import net.labeo.webcore.PageTool;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ViewLayout.java,v 1.1 2005-01-24 04:34:19 pablo Exp $
 */
public class ViewLayout extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String layout = parameters.get("layout");
        SiteResource site = getSite();
        try
        {
            Template layoutTemplate = skinService.getLayoutTemplate(site, skin, layout);
            data.setLayoutTemplate(layoutTemplate);
            data.setPageTemplate("CmsSitePage");
            PageTool pageTool = (PageTool)context.get("page_tool");
            pageTool.addCommonStyleLink("style/cms-component-wrapper.css");
            getCmsData().setSkinName(skin);
            templatingContext.put("layout_preview", Boolean.TRUE);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load template", e);        
        }
    }
}
