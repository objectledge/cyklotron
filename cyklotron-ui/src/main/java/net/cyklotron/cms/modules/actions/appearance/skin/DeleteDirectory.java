package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DeleteDirectory.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class DeleteDirectory extends BaseAppearanceAction
{

    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String path = parameters.get("path");
        String skin = parameters.get("skin");
        path = path.replace(',', '/');
        try
        {
            deleteDirectory(getSite(context), skin, path);
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("appearance,skin,DeleteDirectory");
        }
        else
        {
            templatingContext.put("result","deleted_successfully");
        }
    }
    
    private void deleteDirectory(SiteResource site, String skin, String path)
        throws SkinException
    {
        String[] directories = skinService.getContentDirectoryNames(site, skin, path);
        for(int i=0; i<directories.length; i++)
        {
            deleteDirectory(site, skin, path+"/"+directories[i]);
        }
        String[] files = skinService.getContentFileNames(site, skin, path);
        for(int i=0; i<files.length; i++)
        {
            skinService.deleteContentFile(site, skin, path+"/"+files[i]);
        }
        skinService.deleteContentDirectory(site, skin, path);
    }
}
