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
import net.cyklotron.cms.periodicals.EmailPeriodicalResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResource;
import net.cyklotron.cms.periodicals.PublicationTimeData;
import net.cyklotron.cms.site.SiteResource;

/**
 * Periodical adding action.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalAdd.java,v 1.2 2005-01-24 10:27:17 pablo Exp $
 */
public class PeriodicalAdd
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

		PeriodicalResourceData periodicalData = PeriodicalResourceData.getData(data, null, false);
        periodicalData.update(data);
        
        if(periodicalData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        
        SiteResource site = getSite(context);
        try
        {
			PeriodicalsNodeResource root = null;
			if(periodicalData.isEmailPeriodical())
			{
				root = periodicalsService.getEmailPeriodicalsRoot(site);
			}
			else
			{
				root = periodicalsService.getPeriodicalsRoot(site);
			}
            if(coralSession.getStore().getResource(root, periodicalData.getName()).length > 0)
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
				
            PeriodicalResource periodical = null;
			if(periodicalData.isEmailPeriodical())
			{
				periodical = EmailPeriodicalResourceImpl
                .createEmailPeriodicalResource(coralSession, periodicalData.getName(), root, subject);
			}
			else
			{            
				periodical = PeriodicalResourceImpl
				.createPeriodicalResource(coralSession, periodicalData.getName(), root, subject);
			}
            periodical.setDescription(periodicalData.getDescription());
            periodical.setStorePlace(storePlace);
            periodical.setCategoryQuerySet(categoryQuerySet);
            periodical.setRenderer(periodicalData.getRenderer());
            periodical.setLocale(periodicalData.getLocale());
            periodical.setEncoding(periodicalData.getEncoding());
			if(periodicalData.isEmailPeriodical())
			{
				((EmailPeriodicalResource)periodical).setAddresses(periodicalData.getAddresses());
				((EmailPeriodicalResource)periodical).setFromHeader(periodicalData.getFromHeader());
				((EmailPeriodicalResource)periodical).setFullContent(periodicalData.getFullContent());		
				((EmailPeriodicalResource)periodical).setSubject(periodicalData.getSubject());
			}
            periodical.update(subject);
            updatePublicationTimes(data, periodicalData, periodical, subject);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("problem adding a periodical", e);
            return;
        }
		PeriodicalResourceData.removeData(data, null);
        try
        {
        	if(periodicalData.isEmailPeriodical())
        	{
        		mvcContext.setView("periodicals,EmailPeriodicals");
        	}
        	else
        	{
        	    mvcContext.setView("periodicals,Periodicals");
        	}
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to periodical list", e);
        }
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
