package net.cyklotron.cms.modules.actions.periodicals;

import java.util.Iterator;

import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceImpl;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.DirectoryResourceImpl;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;
import net.cyklotron.cms.periodicals.PublicationTimeData;

/**
 * Periodical update action.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalUpdate.java,v 1.1 2005-01-24 04:34:14 pablo Exp $
 */
public class PeriodicalUpdate
    extends BasePeriodicalsAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

		long periodicalId = parameters.getLong("periodical_id", -1);
		if(periodicalId == -1)
		{
			throw new ProcessingException("Periodical id couldn't be found");
		}
		PeriodicalResource periodical = null;
		PeriodicalResourceData periodicalData = null;
		try
		{
			periodical = PeriodicalResourceImpl.getPeriodicalResource(coralSession, periodicalId);
			periodicalData = PeriodicalResourceData.getData(data, periodical, false);
    	    periodicalData.update(data);
        	if(periodicalData.getName().equals(""))
        	{
            	templatingContext.put("result", "name_empty");
            	return;
        	}
        	if(!periodicalData.getName().equals(periodical.getName()) &&
				coralSession.getStore().getResource(periodical.getParent(), periodicalData.getName()).length > 0)
            {
               	templatingContext.put("result","duplicate_periodical_name");
                return;
            }
            if(periodicalData.getStorePlace()==-1)
            {
				templatingContext.put("result","store_place_not_choosen");
				return;
            }
			if(periodicalData.getCategoryQuerySet()==-1)
			{
				templatingContext.put("result","category_query_set_not_choosen");
				return;
			}
            if(periodicalData.isEmailPeriodical())
            {
            	if(periodicalData.getFromHeader().length() == 0)
            	{
					templatingContext.put("result","from_header_not_choosen");
					return;
            	}
            }
            for(Iterator i = periodicalData.getPublicationTimes().iterator(); i.hasNext();)
            {
                PublicationTimeData time = (PublicationTimeData)i.next();
                if(time.getDayOfMonth() == -1 && time.getDayOfWeek() == -1)
                {
                    templatingContext.put("result", "must_choose_day_of_month_or_week");
                    return;
                }
                if(time.getDayOfMonth() != -1 && time.getDayOfWeek() != -1)
                {
                    templatingContext.put("result", "cant_choose_day_of_month_and_week");
                    return;
                }
            }
            DirectoryResource storePlace = DirectoryResourceImpl.
            	getDirectoryResource(coralSession, periodicalData.getStorePlace());
			CategoryQueryPoolResource categoryQuerySet = CategoryQueryPoolResourceImpl.
				getCategoryQueryPoolResource(coralSession, periodicalData.getCategoryQuerySet());
			periodical.setDescription(periodicalData.getDescription());
            periodical.setStorePlace(storePlace);
            periodical.setCategoryQuerySet(categoryQuerySet);
			periodical.setRenderer(periodicalData.getRenderer());
            periodical.setTemplate(periodicalData.getTemplate());
            periodical.setLocale(periodicalData.getLocale());
            periodical.setEncoding(periodicalData.getEncoding());
			if(periodicalData.isEmailPeriodical())
			{
				((EmailPeriodicalResource)periodical).setAddresses(periodicalData.getAddresses());
				((EmailPeriodicalResource)periodical).setFromHeader(periodicalData.getFromHeader());
	            ((EmailPeriodicalResource)periodical).setSubject(periodicalData.getSubject());
				((EmailPeriodicalResource)periodical).setFullContent(periodicalData.getFullContent());
                ((EmailPeriodicalResource)periodical).setNotificationRenderer(periodicalData.getNotificationRenderer());                        
                ((EmailPeriodicalResource)periodical).setNotificationTemplate(periodicalData.getNotificationTemplate());                		
			}
			if(!periodicalData.getName().equals(periodical.getName()))
			{
				coralSession.getStore().setName(periodical, periodicalData.getName());
			}
            periodical.update(subject);
			updatePublicationTimes(data, periodicalData, periodical, subject);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
            log.error("problem adding a periodical", e);
            return;
        }
		PeriodicalResourceData.removeData(data, null);
        try
        {
        	if(periodicalData.isEmailPeriodical())
        	{
        		data.setView("periodicals,EmailPeriodicals");
        	}
        	else
        	{
        	    data.setView("periodicals,Periodicals");
        	}
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to periodical list", e);
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return true;
    }
}
