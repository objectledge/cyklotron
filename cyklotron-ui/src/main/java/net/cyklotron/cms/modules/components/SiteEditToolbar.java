package net.cyklotron.cms.modules.components;

import java.util.Calendar;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.skins.SkinService;

public class SiteEditToolbar
    extends BaseCMSComponent
{
    public SiteEditToolbar(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory)
    {
        super(context, logger, templating, cmsDataFactory);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String siteName = getSite(context).getName();

        // setup skin preview
        String key = SkinService.PREVIEW_KEY_PREFIX + siteName;
        String preview = (String)httpContext.getSessionAttribute(key);
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
