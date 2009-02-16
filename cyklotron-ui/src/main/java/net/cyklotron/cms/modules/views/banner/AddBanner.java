package net.cyklotron.cms.modules.views.banner;

import java.util.Calendar;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 *
 */
public class AddBanner
    extends BaseBannerScreen
{
    
    public AddBanner(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, BannerService bannerService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, bannerService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        Calendar calendar = Calendar.getInstance(i18nContext.getLocale());
        templatingContext.put("calendar",calendar);
        Calendar twoWeeksLater = Calendar.getInstance(i18nContext.getLocale());
        twoWeeksLater.add(Calendar.DAY_OF_MONTH,14);
        templatingContext.put("two_weeks_later",twoWeeksLater);
    }    
}
