package net.cyklotron.cms.syndication;

import org.objectledge.parameters.Parameters;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.category.query.CategoryQueryResource;


/**
 * Provides default values and state keeping for resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OutgoingFeedResourceData.java,v 1.1.6.1 2005-08-04 10:32:25 pablo Exp $
 */
public class OutgoingFeedResourceData
{
    public static OutgoingFeedResourceData getData(HttpContext httpContext, OutgoingFeedResource feed)
    {
        String key = getDataKey(feed);
        OutgoingFeedResourceData currentData = (OutgoingFeedResourceData)
        httpContext.getSessionAttribute(key);
        if(currentData == null)
        {
            currentData = new OutgoingFeedResourceData(feed);
            httpContext.setSessionAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(HttpContext httpContext, OutgoingFeedResource feed)
    {
        httpContext.removeSessionAttribute(getDataKey(feed));
    }

    private static String getDataKey(OutgoingFeedResource feed)
    {
        if(feed != null)
        {
            return "cms.syndication.outgoing.feed.data."+feed.getIdString();
        }
        else
        {
            return "cms.syndication.outgoing.feed.data.NEW";
        }
    }
    
    private String name;
    private String description;
    private int interval;
    private String template;
    private String queryName;
    private boolean publik;
    // RSS params
    private String category;
    private String copyright;
    private String lang;
    private String managingEditor;
    private String webmaster;
    
    
    private String sortColumn;
    private boolean sortOrder;
    private int publicationTimeOffset;
    private int maxResNumber;

    /** <code>true</code> if the config object was created during current request. */
    private boolean newData;

    public OutgoingFeedResourceData(OutgoingFeedResource feed)
    {
        if(feed != null)
        {
            name = feed.getName();
            description = feed.getDescription();
            interval = feed.getInterval();
            template = feed.getGenerationTemplate();
            CategoryQueryResource query = feed.getCategoryQuery();
            if(query != null)
            {
                queryName = query.getName();
            }
            publik = feed.getPublic();
            // RSS params
            category = feed.getCategory();
            copyright = feed.getCopyright();
            lang = feed.getLanguage();
            managingEditor = feed.getManagingEditor();
            webmaster = feed.getWebMaster();
            sortOrder = feed.getSortOrder(false);
            sortColumn = feed.getSortColumn();
            publicationTimeOffset = feed.getOffset(0);
            maxResNumber = feed.getLimit(0);
            
        }
        newData = true;
    } 

    public boolean isNew()
    {
        return newData;
    }

    public void update(Parameters params)
    {
        name = params.get("name", null);
        description = params.get("description", null);
        interval = params.getInt("interval", -1);
        template = params.get("template", null);
        queryName = params.get("queryName", null);
        publik = params.getBoolean("public", false);
        // RSS params
        category = params.get("category", null);
        copyright = params.get("copyright", null);
        lang = params.get("lang", null);
        managingEditor = params.get("managingEditor", null);
        webmaster = params.get("webmaster", null);
        
        sortOrder = params.getBoolean("listSortDir",false);
        sortColumn = params.get("listSortColumn","");
        publicationTimeOffset = params.getInt("publicationTimeOffset", 0);
        maxResNumber = params.getInt("maxResNumber", 0);

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

    public int getInterval()
    {
        return interval;
    }

    public String getTemplate()
    {
        return template;
    }

    public String getCategory()
    {
        return category;
    }

    public String getCopyright()
    {
        return copyright;
    }

    public String getLang()
    {
        return lang;
    }

    public String getManagingEditor()
    {
        return managingEditor;
    }

    public boolean getPublic()
    {
        return publik;
    }

    public String getQueryName()
    {
        return queryName;
    }

    public String getWebmaster()
    {
        return webmaster;
    }

    /**
     * @return Returns the maxResNumber.
     */
    public int getMaxResNumber()
    {
        return maxResNumber;
    }

    /**
     * @param maxResNumber The maxResNumber to set.
     */
    public void setMaxResNumber(int maxResNumber)
    {
        this.maxResNumber = maxResNumber;
    }

    /**
     * @return Returns the publicationTimeOffset.
     */
    public int getPublicationTimeOffset()
    {
        return publicationTimeOffset;
    }

    /**
     * @param publicationTimeOffset The publicationTimeOffset to set.
     */
    public void setPublicationTimeOffset(int publicationTimeOffset)
    {
        this.publicationTimeOffset = publicationTimeOffset;
    }

    /**
     * @return Returns the sortColumn.
     */
    public String getSortColumn()
    {
        return sortColumn;
    }

    /**
     * @param sortColumn The sortColumn to set.
     */
    public void setSortColumn(String sortColumn)
    {
        this.sortColumn = sortColumn;
    }

    /**
     * @return Returns the sortOrder.
     */
    public boolean getSortOrder()
    {
        return sortOrder;
    }

    /**
     * @param sortOrder The sortOrder to set.
     */
    public void setSortOrder(boolean sortOrder)
    {
        this.sortOrder = sortOrder;
    }
}
