package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.upload.UploadContainer;
import net.labeo.services.upload.UploadService;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateFile.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class CreateFile extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        UploadService uploadService = (UploadService)data.getBroker().
            getService(UploadService.SERVICE_NAME);
        Context context = data.getContext();
        String path = parameters.get("path");
        String name = parameters.get("name");
        String skin = parameters.get("skin");
        UploadContainer file = uploadService.getItem(data, "file");
        if(name.length() == 0)
        {
            name = file.getFileName();
        }
        path = path.replace(',', '/') + name;
        SiteResource site = getSite(context);
        try
        {
            if(skinService.contentItemExists(site, skin, path))
            {   
                templatingContext.put("result","file_or_directory_exists");
            }
            else
            {
                if(file == null)
                {
                    templatingContext.put("result","file_not_selected");
                }
                else
                {
                    skinService.createContentFile(site, skin, path, file.getInputStream());
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,CreateFile");
        }
        else
        {
            templatingContext.put("result","file_created");
        }
    }
}
