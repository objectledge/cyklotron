/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.periodicals;

import java.util.Locale;

import net.labeo.services.templating.Context;
import net.labeo.services.upload.UploadContainer;
import net.labeo.services.upload.UploadService;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CreateTemplate 
    extends BasePeriodicalsAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException, NotFoundException
    {
        UploadService uploadService = (UploadService)data.getBroker().
            getService(UploadService.SERVICE_NAME);
        Context context = data.getContext();
        SiteResource site = getSite(context);

        String renderer = parameters.get("renderer");
        String name = parameters.get("name");
        if(name.length() == 0)
        {
            templatingContext.put("result", "name_empty");
        }
        if(periodicalsService.hasTemplateVariant(site, renderer, name))
        {
            templatingContext.put("result", "name_in_use");
        }
    
        if(!templatingContext.containsKey("result"))
        {
            String source = parameters.get("source","app");
            UploadContainer file = uploadService.getItem(data, "file");
            try
            {
                String contents = null;
                if(source.equals("file"))
                {
                    if(file == null)
                    {
                        templatingContext.put("result","file_not_selected");
                    }
                    else
                    {
                        contents = file.getString();
                    }
                }
                else if(source.equals("app"))
                {
                    Locale locale = StringUtils.getLocale(parameters.
                        get("locale"));
                    contents = periodicalsService.getDefaultTemplateContents(renderer, locale);
                }
                else if(source.equals("variant"))
                {
                    String variant = parameters.get("variant");
                    contents = periodicalsService.getTemplateVariantContents(site, renderer, variant);
                }
                else
                {
                    contents = "";
                }
                periodicalsService.createTemplateVariant(site, renderer, name, contents);                
            }
            catch(Exception e)
            {
                templatingContext.put("result", "exception");
                templatingContext.put("trace", new StackTrace(e));
            }
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("periodicals,CreateTemplate");
        }
        else
        {
            mvcContext.setView("periodicals,EditTemplate");
            templatingContext.put("result","added_successfully");
        }
    }
}
