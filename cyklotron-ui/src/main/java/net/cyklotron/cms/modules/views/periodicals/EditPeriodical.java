package net.cyklotron.cms.modules.views.periodicals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.labeo.services.table.TableService;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.WebcoreService;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;

/**
 * Periodical edit screen. 
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: EditPeriodical.java,v 1.1 2005-01-24 04:34:37 pablo Exp $
 */
public class EditPeriodical 
    extends BasePeriodicalsScreen
{
    protected TableService tableService;

    protected WebcoreService webcoreService;

    public EditPeriodical()
        throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        webcoreService = (WebcoreService)broker.getService(WebcoreService.SERVICE_NAME);
    }

    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
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
			if (parameters.get("from_list").asBoolean(false))
			{
			    PeriodicalResourceData.removeData(data, periodical);
			}
			boolean email = parameters.getBoolean("email_periodical", false);
			PeriodicalResourceData periodicalData = PeriodicalResourceData.getData(data, periodical,email);
			templatingContext.put("periodical_data", periodicalData);
			if (periodicalData.isNew())
			{
			    periodicalData.init(coralSession, periodical);
			}
			else
			{
			    periodicalData.update(data);
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
                templates.put(renderers[i], periodicalsService.getTemplateVariants(getSite(), renderers[i]));
            }
            templatingContext.put("templates", templates);
            Map locales = new HashMap();
            List supported = webcoreService.getSupportedLocales();
            for(Iterator i=supported.iterator(); i.hasNext();)
            {
                java.util.Locale locale = (java.util.Locale)i.next();
                String desc = webcoreService.getLocaleDescription(locale);
                locales.put(locale, desc);
            }
            templatingContext.put("locales", locales);
    	}
    	catch(Exception e)
    	{
    		throw new ProcessingException("Exception occured", e); 
    	}
    }
}
