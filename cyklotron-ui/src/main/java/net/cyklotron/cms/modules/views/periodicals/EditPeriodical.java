package net.cyklotron.cms.modules.views.periodicals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18n;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Periodical edit screen. 
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: EditPeriodical.java,v 1.4 2006-05-04 11:53:53 rafal Exp $
 */
public class EditPeriodical 
    extends BasePeriodicalsScreen
{
    protected I18n i18n;
    private final PeriodicalsTemplatingService periodicalsTemplatingService;
    
    public EditPeriodical(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PeriodicalsService periodicalsService,
        PeriodicalsTemplatingService periodicalsTemplatingService, I18n i18n)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        periodicalsService);
        this.periodicalsTemplatingService = periodicalsTemplatingService;
        this.i18n = i18n;
    }
    /* 
     * (overriden)
     */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
    	long perId = parameters.getLong("periodical_id", -1);
    	PeriodicalResource periodical = null;
    	try
    	{
        	if(perId != -1)
        	{
        		periodical = PeriodicalResourceImpl.getPeriodicalResource(coralSession, perId);
        		templatingContext.put("periodical", periodical);
        	}
	        // get pool resource data
			if (parameters.getBoolean("from_list",false))
			{
			    PeriodicalResourceData.removeData(httpContext, periodical);
			}
			boolean email = parameters.getBoolean("email_periodical", false);
			PeriodicalResourceData periodicalData = PeriodicalResourceData.getData(httpContext, periodical,email);
			templatingContext.put("periodical_data", periodicalData);
			if (periodicalData.isNew())
			{
			    periodicalData.init(coralSession, periodical);
			}
			else
			{
			    periodicalData.update(parameters);
			}
			long categoryQuerySetId = periodicalData.getCategoryQuerySet();
			if(categoryQuerySetId != -1)
			{
				templatingContext.put("category_query_set",coralSession.getStore().getResource(categoryQuerySetId));
			}
			long storePlaceId = periodicalData.getStorePlace();
			if(storePlaceId != -1)
			{	
				templatingContext.put("store_place", coralSession.getStore().getResource(storePlaceId));
			}
			List daysOfMonth = new ArrayList();
			for(int i = 1; i <= 31; i++)
    		{
    			daysOfMonth.add(new Integer(i));
    		}
			List daysOfWeek = new ArrayList();
			for(int i = 1; i <= 7; i++)
			{
				daysOfWeek.add(new Integer(i));
			}
			List hours = new ArrayList();
			for(int i = 0; i <= 23; i++)
			{
				hours.add(new Integer(i));
			}
			templatingContext.put("days_of_month", daysOfMonth);
			templatingContext.put("days_of_week", daysOfWeek);
			templatingContext.put("hours", hours);
            String[] renderers = periodicalsService.getRendererNames();
			templatingContext.put("renderers", renderers);
            Map templates = new HashMap(renderers.length);
            for(int i=0; i<renderers.length; i++)
            {
                templates.put(renderers[i], periodicalsTemplatingService.getTemplateVariants(
                    getSite(), renderers[i]));
            }
            templatingContext.put("templates", templates);
            Map locales = new HashMap();
            Locale[] supported = i18n.getSupportedLocales();
            for(int i = 0; i < supported.length; i++)
            {
                String desc = i18n.getLocaleName(supported[i]);
                locales.put(supported[i], desc);
            }
            templatingContext.put("locales", locales);
    	}
    	catch(Exception e)
    	{
    		throw new ProcessingException("Exception occured", e); 
    	}
    }
}
