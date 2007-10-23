package net.cyklotron.cms.periodicals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

/**
 * Provides default values and state keeping for periodical resource editing.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalResourceData.java,v 1.5 2007-10-23 20:54:46 rafal Exp $
 */
public class PeriodicalResourceData
{
    public static PeriodicalResourceData getData(HttpContext httpContext, PeriodicalResource periodical, boolean email)
    {
        String key = getDataKey(periodical);
        PeriodicalResourceData currentData = (PeriodicalResourceData)httpContext.getSessionAttribute(key);
        if (currentData == null)
        {
            currentData = new PeriodicalResourceData(email);
            httpContext.setSessionAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(HttpContext httpContext, PeriodicalResource periodical)
    {
        httpContext.removeSessionAttribute(getDataKey(periodical));
    }

    private static String getDataKey(PeriodicalResource periodical)
    {
        if (periodical != null)
        {
            return "cms.periodical.periodical.data." + periodical.getIdString();
        }
        else
        {
            return "cms.periodical.periodical.data.NEW";
        }
    }

    private String name;
    private String description;
    private long storePlaceId;
    private long categoryQuerySetId;
    private String sortOrder;
    private String sortDirection;
    private Date lastPublished;
    private boolean emailPeriodical;
    private String addresses;
    private String fromHeader;
    private boolean fullContent;
    private String notificationRenderer;
    private String notificationTemplate;
    private List publicationTimes;
    private String renderer;
    private String template;
    private String locale;
    private String encoding;
    private String subject;

    /** <code>true</code> if the config object was created during current request. */
    private boolean newData;

    public PeriodicalResourceData(boolean email)
    {
        newData = true;
        name = "";
        description = "";
        storePlaceId = -1;
        categoryQuerySetId = -1;
        sortOrder = "priority.validity.start";
        sortDirection = "asc";
        lastPublished = null;
        addresses = "";
        fromHeader = "";
        renderer = "";
        template = "";
        locale = "";
        encoding = "";
        subject = "";
        fullContent = false;
        emailPeriodical = email;
        notificationRenderer = "";
        notificationTemplate = "";
        publicationTimes = new ArrayList();
        publicationTimes.add(new PublicationTimeData(-1,-1,-1));
    }

    public boolean isNew()
    {
        return newData;
    }

    public void init(CoralSession resourceService, PeriodicalResource periodical) throws ProcessingException
    {
        if (periodical != null)
        {
            name = periodical.getName();
            description = periodical.getDescription();
            storePlaceId = periodical.getStorePlace().getId();
            categoryQuerySetId = periodical.getCategoryQuerySet().getId();
            sortOrder = periodical.getSortOrder();
            sortDirection = periodical.getSortDirection();
            lastPublished = periodical.getLastPublished();
            renderer = periodical.getRenderer();
            template = periodical.getTemplate();
            locale = periodical.getLocale();
            encoding = periodical.getEncoding();
            if (periodical instanceof EmailPeriodicalResource)
            {
                emailPeriodical = true;
                addresses = ((EmailPeriodicalResource)periodical).getAddresses();
                fromHeader = ((EmailPeriodicalResource)periodical).getFromHeader();
                fullContent = ((EmailPeriodicalResource)periodical).getFullContent();
                notificationRenderer = ((EmailPeriodicalResource)periodical).getNotificationRenderer();
                notificationTemplate = ((EmailPeriodicalResource)periodical).getNotificationTemplate();
                subject = ((EmailPeriodicalResource)periodical).getSubject();
            }
            else
            {
                emailPeriodical = false;
                addresses = "";
                fromHeader = "";
                subject = "";
                notificationRenderer = "";
                notificationTemplate = "";
                fullContent = false;
            }
			publicationTimes = new ArrayList();
			Resource[] resources = resourceService.getStore().getResource(periodical);
			for(int i = 0; i < resources.length; i++)
			{
				PublicationTimeResource publicationTime = (PublicationTimeResource)resources[i];
				PublicationTimeData publicationTimeData = new PublicationTimeData(publicationTime.getDayOfMonth(),
																				  publicationTime.getDayOfWeek(),
																				  publicationTime.getHour());
				publicationTimes.add(publicationTimeData);																				
			}
        }
        // data was modified
        newData = false;
    }

    public void update(Parameters params) throws ProcessingException
    {
        name = params.get("name","");
        description = params.get("description","");
        renderer = params.get("renderer","");
        template = params.get("template","");
        locale = params.get("locale","");
        encoding = params.get("encoding","");
        // add directory & others...
        categoryQuerySetId = params.getLong("category_query_set_id",-1);
        sortOrder = params.get("sort_order", "");
        sortDirection = params.get("sort_direction", "");
        storePlaceId = params.getLong("store_place_id",-1);
        fromHeader = params.get("from_header","");
        subject = params.get("subject","");
        addresses = params.get("addresses","");
        fullContent = params.getBoolean("full_content",false);
        notificationRenderer = params.get("notification_renderer","");
        notificationTemplate = params.get("notification_template","");
        if(params.getBoolean("last_published_enabled", false))
        {
            lastPublished = new Date(params.getLong("last_published"));
        }
        else
        {
            lastPublished = null;
        }
        publicationTimes = new ArrayList();
		int[] keys = params.getInts("publication_times");
		for(int i = 0; i < keys.length; i++)
		{
			int counter = keys[i];
			//int counter = i + 1;
			PublicationTimeData ptd = new PublicationTimeData(params.getInt("day_of_month_"+counter),
															  params.getInt("day_of_week_"+counter),
															  params.getInt("hour_"+counter));
		    publicationTimes.add(ptd);
		}
        // data was modified
        newData = false;
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public long getStorePlace()
    {
        return storePlaceId;
    }

    public long getCategoryQuerySet()
    {
        return categoryQuerySetId;
    }
    
    public String getSortOrder()
    {
        return sortOrder;
    }
    
    public String getSortDirection()
    {
        return sortDirection;
    }

    public Date getLastPublished()
    {
        return lastPublished;
    }

    public String getAddresses()
    {
        return addresses;
    }

    public String getFromHeader()
    {
        return fromHeader;
    }

	public String getRenderer()
	{
		return renderer;
	}

    public String getTemplate()
    {
        return template;
    }
    
    public String getLocale()
    {
        return locale;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public String getSubject()
    {
        return subject;
    }
    
    public boolean getFullContent()
    {
        return fullContent;
    }

    public String getNotificationRenderer()
    {
        return notificationRenderer;
    }

    public String getNotificationTemplate()
    {
        return notificationTemplate;
    }
    
    public boolean isEmailPeriodical()
    {
    	return emailPeriodical;
    }
    
    public List getPublicationTimes()
    {
    	return publicationTimes;
    }
}
