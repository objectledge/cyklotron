package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ViewFile.java,v 1.2 2005-01-25 11:23:41 pablo Exp $
 */
public class ViewFile extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String path = parameters.get("path");
        SiteResource site = getSite();
        templatingContext.put("skin", skin);
        templatingContext.put("path", path);
        path = path.replace(',', '/');
        templatingContext.put("path_slashes", path);
        int last = path.lastIndexOf('/');
        String fileName = last >= 0 ? path.substring(last + 1) : path;
        templatingContext.put("file_name", fileName);
        templatingContext.put(
            "path_link",
            "/sites/" + site.getName() + "/" + skin + "/" + path);
        try
        {
            String type = skinService.getContentFileType(site, skin, path);
            templatingContext.put("mime_type", type);
            if (type.startsWith("text/"))
            {
                String contents =
                    skinService.getContentFileContents(
                        site,
                        skin,
                        path,
                        data.getEncoding());
                contents = StringUtils.escapeXMLCharacters(contents);
                templatingContext.put("contents", contents);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to load file contents", e);
        }
    }
}
