package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EditFile.java,v 1.1 2005-01-24 04:34:19 pablo Exp $
 */
public class EditFile extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String path = parameters.get("path");
        SiteResource site = getSite();
        templatingContext.put("skin", skin);
        templatingContext.put("path", path);
        path = path.replace(',','/');
        int last = path.lastIndexOf('/');
        String fileName = last >= 0 ? path.substring(last+1) : path;
        templatingContext.put("file_name", fileName);
        templatingContext.put("path_slashes", path);
        try
        {
            String type = skinService.getContentFileType(site, skin, path);
            templatingContext.put("mime_type", type);
            templatingContext.put("contents", skinService.getContentFileContents(site, skin, path, data.getEncoding()));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load file contents", e);
        }
    }
}
