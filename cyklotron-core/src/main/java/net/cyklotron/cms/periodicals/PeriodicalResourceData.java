package net.cyklotron.cms.periodicals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Provides default values and state keeping for periodical resource editing.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalResourceData.java,v 1.1 2005-01-12 20:45:08 pablo Exp $
 */
public class PeriodicalResourceData
{
    public static PeriodicalResourceData getData(RunData data, PeriodicalResource periodical, boolean email)
    {
        String key = getDataKey(periodical);
        PeriodicalResourceData currentData = (PeriodicalResourceData)data.getGlobalContext().getAttribute(key);
        if (currentData == null)
        {
            currentData = new PeriodicalResourceData(email);
            data.getGlobalContext().setAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(RunData data, PeriodicalResource periodical)
    {
        data.getGlobalContext().removeAttribute(getDataKey(periodical));
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

    public void init(ResourceService resourceService, PeriodicalResource periodical) throws ProcessingException
    {
        if (periodical != null)
        {
            name = periodical.getName();
            description = periodical.getDescription();
            storePlaceId = periodical.getStorePlace().getId();
            categoryQuerySetId = periodical.getCategoryQuerySet().getId();
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

    public void update(RunData data) throws ProcessingException
    {
        ParameterContainer params = data.getParameters();
        name = params.get("name").asString("");
        description = params.get("description").asString("");
        renderer = params.get("renderer").asString("");
        template = params.get("template").asString("");
        locale = params.get("locale").asString("");
        encoding = params.get("encoding").asString("");
        // add directory & others...
        categoryQuerySetId = params.get("category_query_set_id").asLong(-1);
        storePlaceId = params.get("store_place_id").asLong(-1);
        fromHeader = params.get("from_header").asString("");
        subject = params.get("subject").asString("");
        addresses = params.get("addresses").asString("");
        fullContent = params.get("full_content").asBoolean(false);
        notificationRenderer = params.get("notification_renderer").asString("");
        notificationTemplate = params.get("notification_template").asString("");
        publicationTimes = new ArrayList();
		Parameter[] keys = params.getArray("publication_times");
		for(int i = 0; i < keys.length; i++)
		{
			int counter = keys[i].asInt();
			//int counter = i + 1;
			PublicationTimeData ptd = new PublicationTimeData(params.get("day_of_month_"+counter).asInt(),
															  params.get("day_of_week_"+counter).asInt(),
															  params.get("hour_"+counter).asInt());
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
