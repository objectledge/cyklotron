package net.cyklotron.cms.search.searching.netsprint;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import net.cyklotron.cms.search.searching.SearchHit;

/**
 * This class represents a netsprint search result.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NetSprintSearchHit.java,v 1.2 2005-02-09 22:20:56 rafal Exp $
 */
public class NetSprintSearchHit implements SearchHit
{
    private String title;
    private String abbreviation;
    private String url;
    private int score;
    private Date modificationTime;
    
    private Map attributes;

    public NetSprintSearchHit(Attributes attr, DateFormat format)
    	throws ParseException
    {
        this.score = Integer.parseInt(attr.getValue("score"));
        String modTimeVal = attr.getValue("retrieval-date");
        if(modTimeVal != null)
        {
			this.modificationTime = format.parse(modTimeVal);
        }
        else
        {
			this.modificationTime = null;
        }

        attributes = new HashMap();
        attributes.put("number", attr.getValue("number"));
        attributes.put("size", attr.getValue("size"));
        attributes.put("archiveId", attr.getValue("archive-id"));
        attributes.put("isIndented", attr.getValue("is-intended"));
    }

    public String get(String name)
    {
        return (String)attributes.get(name);
    }

    public Date getAsDate(String fieldName)
    {
        return null;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    /**
     * Returns the modification time of the resource.
     *
     * @return the object representing the modification time of the resource
     */
    public Date getModificationTime()
    throws Exception
    {
        return modificationTime;
    }

    /**
     * Returns hit score in promiles.
     *
     * @return the number representing promiles of search relevancy.
     */
    public int getScore()
    {
        return score*10;
    }

    void setAbbreviation(String string)
    {
        abbreviation = string;
    }

    void setTitle(String string)
    {
        title = string;
    }

    void setUrl(String string)
    {
        url = string;
    }

	void set(String name, String string)
	{
		attributes.put(name, string);
	}
}
