package net.cyklotron.cms.modules.views.banner;

import java.util.Calendar;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 */
public class AddBanner
    extends BaseBannerScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        Calendar calendar = Calendar.getInstance(i18nContext.getLocale()());
        templatingContext.put("calendar",calendar);
        Calendar twoWeeksLater = Calendar.getInstance(i18nContext.getLocale()());
        twoWeeksLater.add(Calendar.DAY_OF_MONTH,14);
        templatingContext.put("two_weeks_later",twoWeeksLater);
    }    
}
