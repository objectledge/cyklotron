package net.cyklotron.cms.modules.components;

import java.util.Calendar;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.skins.SkinService;

public class SiteEditToolbar
    extends BaseCMSComponent
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        String siteName = getSite(context).getName();

        // setup skin preview
        String key = SkinService.PREVIEW_KEY_PREFIX + siteName;
        String preview = (String)data.getGlobalContext().getAttribute(key);
        templatingContext.put("skin_preview", preview);
        
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(cmsData.getBrowseMode().equals("time_travel"))
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cmsData.getDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            templatingContext.put("next_day",calendar.getTime());
            calendar.setTime(cmsData.getDate());
            calendar.add(Calendar.HOUR, 1);
            templatingContext.put("next_hour",calendar.getTime());
            calendar.setTime(cmsData.getDate());
            calendar.add(Calendar.MONTH, 1);
            templatingContext.put("next_month",calendar.getTime());
            calendar.setTime(cmsData.getDate());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            templatingContext.put("prev_day",calendar.getTime());
            calendar.setTime(cmsData.getDate());
            calendar.add(Calendar.HOUR, -1);
            templatingContext.put("prev_hour",calendar.getTime());
            calendar.setTime(cmsData.getDate());
            calendar.add(Calendar.MONTH, -1);
            templatingContext.put("prev_month",calendar.getTime());
        }
    }
}
