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
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateLayout.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class CreateLayout extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        UploadService uploadService = (UploadService)data.getBroker().
            getService(UploadService.SERVICE_NAME);
        Context context = data.getContext();
        String layout = parameters.get("layout");
        String skin = parameters.get("skin");
        boolean useFile = parameters.get("source").
            equals("file");
        UploadContainer file = uploadService.getItem(data, "file");
        SiteResource site = getSite(context);
        try
        {
            String contents = null;
            if(file == null)
            {
                if(useFile == true)
                {
                    templatingContext.put("result","file_not_selected");
                }
                else
                {
                    contents = "";
                }
            }
            else
            {
                contents = file.getString();
            }
                
            if(contents != null)
            {
                skinService.createLayoutTemplate(site, skin, layout, contents, coralSession.getUserSubject());
            }
            
            String[] templateSockets = null;
            try
            {
                styleService.findSockets(contents);
            }
            catch(StyleException e)
            {
                templatingContext.put("result", "template_saved_parse_error");
                templatingContext.put("parse_trace", StringUtils.stackTrace(e.getRootCause()));
                data.setView("appearance,skin,EditLayout");
                return;
            }
            if(templateSockets != null)
            {
                LayoutResource layoutRes = styleService.getLayout(site, layout);
                if(!styleService.matchSockets(layoutRes, templateSockets))
                {
                    data.setView("appearance,skin,ValidateLayout");
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
            data.setView("appearance,skin,CreateLayout");
        }
        else
        {
            templatingContext.put("result","file_created");
        }
    }
}
