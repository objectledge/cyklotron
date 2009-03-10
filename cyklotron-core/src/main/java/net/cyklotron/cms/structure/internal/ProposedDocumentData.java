package net.cyklotron.cms.structure.internal;

import java.util.Date;

import org.objectledge.parameters.Parameters;
import org.objectledge.templating.TemplatingContext;

/**
 * Data object used by ProposeDocument view and action.
 * <p>
 * Feels kind like breaking open door, but I'm not willing to learn formtool just to make one silly
 * screen.
 * </p>
 * 
 * @author rafal
 */
public class ProposedDocumentData
{
    String name;
    String title;
    String docAbstract;
    String content;
    String eventPlace;
    String organizedBy;
    String organizedAddress;
    String organizedPhone;
    String organizedFax;
    String organizedEmail;
    String organizedWww;
    String sourceName;
    String sourceUrl;
    String proposerCredentials;
    String proposerEmail;
    String description;
    Date validityStart;
    Date validityEnd;
    Date eventStart;
    Date eventEnd;
    boolean calendarTree;
    boolean inheritCategories;
    long categoryIds[];

    public void fromParameters(Parameters parameters)
    {
        name = parameters.get("name", "");
        title = parameters.get("title", "");
        docAbstract = parameters.get("abstract", "");
        content = parameters.get("content", "");
        eventPlace = parameters.get("event_place", "");
        organizedBy = parameters.get("organized_by", "");
        organizedAddress = parameters.get("organized_address", "");
        organizedPhone = parameters.get("organized_phone", "");
        organizedFax = parameters.get("organized_fax", "");
        organizedEmail = parameters.get("organized_email", "");
        organizedWww = parameters.get("organized_www", "");
        sourceName = parameters.get("source_name", "");
        sourceUrl = parameters.get("source_url", "");
        proposerCredentials = parameters.get("proposer_credentials", "");
        proposerEmail = parameters.get("proposer_email", "");
        description = parameters.get("description", "");
        
        validityStart = getDate(parameters, "validity_start");
        validityEnd = getDate(parameters, "validity_end");
        eventStart = getDate(parameters, "event_start");
        eventEnd = getDate(parameters, "event_end");
        
        calendarTree = parameters.getBoolean("calendar_tree", false);
        inheritCategories = parameters.getBoolean("inherit_categories", false);
        
        categoryIds = parameters.getLongs("category_ids");
    }

    /**
     * Transfers the data into the templating context. 
     * 
     * <p>
     * This is needed to keep the exiting templates working
     * </p>
     * 
     * @param templatingContext
     */
    public void toTemplatingContext(TemplatingContext templatingContext)
    {
        templatingContext.put("calendar_tree", calendarTree);
        templatingContext.put("inherit_categories", inheritCategories);
        templatingContext.put("name", name);
        templatingContext.put("title", title);
        templatingContext.put("abstract", docAbstract);
        templatingContext.put("content", content);
        templatingContext.put("event_place", eventPlace);
        templatingContext.put("organized_by", organizedBy);
        templatingContext.put("organized_address", organizedAddress);
        templatingContext.put("organized_phone", organizedPhone);
        templatingContext.put("organized_fax", organizedFax);
        templatingContext.put("organized_email", organizedEmail);
        templatingContext.put("organized_www", organizedWww);
        templatingContext.put("source_name", sourceName);
        templatingContext.put("source_url", sourceUrl);
        templatingContext.put("proposer_credentials", proposerCredentials);
        templatingContext.put("proposer_email", proposerEmail);
        templatingContext.put("description", description);
        setDate(templatingContext, "validity_start", validityStart);
        setDate(templatingContext, "validity_end", validityEnd);
        setDate(templatingContext, "event_start", eventStart);
        setDate(templatingContext, "event_end", eventEnd);
        templatingContext.put("category_ids", categoryIds);        
    }

    // getters
    
    public String getName()
    {
        return name;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDoc_abstract()
    {
        return docAbstract;
    }

    public String getContent()
    {
        return content;
    }

    public String getEventPlace()
    {
        return eventPlace;
    }

    public String getOrganizedBy()
    {
        return organizedBy;
    }

    public String getOrganizedAddress()
    {
        return organizedAddress;
    }

    public String getOrganizedPhone()
    {
        return organizedPhone;
    }

    public String getOrganizedFax()
    {
        return organizedFax;
    }

    public String getOrganizedEmail()
    {
        return organizedEmail;
    }

    public String getOrganizedWww()
    {
        return organizedWww;
    }

    public String getSourceName()
    {
        return sourceName;
    }

    public String getSourceUrl()
    {
        return sourceUrl;
    }

    public String getProposerCredentials()
    {
        return proposerCredentials;
    }

    public String getProposerEmail()
    {
        return proposerEmail;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getValidityStart()
    {
        return validityStart;
    }

    public Date getValidityEnd()
    {
        return validityEnd;
    }

    public Date getEventStart()
    {
        return eventStart;
    }

    public Date getEventEnd()
    {
        return eventEnd;
    }

    public boolean isCalendarTree()
    {
        return calendarTree;
    }

    public boolean isInheritCategories()
    {
        return inheritCategories;
    }

    public long[] getCategoryIds()
    {
        return categoryIds;
    }
    
    // utitily

    private Date getDate(Parameters parameters, String key)
    {
        if(parameters.isDefined(key) && parameters.get(key).trim().length() > 0)
        {
            return parameters.getDate(key);
        }
        else
        {
            return null;
        }
    }
    
    private void setDate(TemplatingContext templatingContext, String key, Date value)
    {
        if(value != null)
        {
            templatingContext.put(key, value.getTime());
        }
    }
}
