package net.cyklotron.cms.search.searching.cms;

import java.text.ParseException;
import java.util.Date;

import org.apache.lucene.document.Document;

import net.cyklotron.cms.search.SearchConstants;
import net.cyklotron.cms.search.SearchUtil;
import net.cyklotron.cms.search.searching.SearchHit;

/**
 * This class wraps up a lucene document which is a search result.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LuceneSearchHit.java,v 1.5 2007-01-28 11:38:48 rafal Exp $
 */
public class LuceneSearchHit
implements SearchHit
{
    private Document doc;
    private float score;
    private String url;
    private String editUrl;
    
    private SH sh;

    public LuceneSearchHit(Document doc, float score)
    {
        this.doc = doc;
        this.score = score;

        this.sh = new RealSH(doc, score);
    }
    
    public boolean equals(Object obj)
    {
        if(obj == null)
        {
            return false;
        }
        LuceneSearchHit other = (LuceneSearchHit)obj;
        return other.getId() == getId();
    }
    
    public int hashCode()
    {
        return (int)getId();
    }

    public String getTitle()
    {
        return sh.get(SearchConstants.FIELD_INDEX_TITLE);
    }

    public String getAbbreviation()
    {
        return sh.get(SearchConstants.FIELD_INDEX_ABBREVIATION);
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public String getEditUrl()
    {
        return editUrl;
    }
    
    /**
     * Returns an arbitrary lucene document field upon it's name.
     * If this hit is not accessible it will return a <code>NO_ACCESS</code> string.
     *
     * @param fieldName name of the field to be retrieved.
     * @return the value of the chosen field
     */
    public String get(String fieldName)
    {
        return sh.get(fieldName);
    }

    public Date getAsDate(String fieldName)
    {
        String dateString = sh.get(fieldName);
        try
        {
            if(dateString != null)
            {
                return SearchUtil.dateFromString(dateString);
            }
            return null;
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    public boolean isPlainDate(String fieldName)
    {
        if(get(fieldName) != null)
        {
            return isPlainDate(getAsDate(fieldName));
        }
        return false;
    }

    public boolean isPlainDate(Date date)
    {
        return !isMinDate(date) && !isMaxDate(date);
    }

    public boolean isMinDate(String fieldName)
    {
        if(get(fieldName) != null)
        {
            return isMinDate(getAsDate(fieldName));
        }
        return false;
    }

    public boolean isMinDate(Date date)
    {
        return date.getTime() == 0L;
    }
    
    public boolean isMaxDate(String fieldName)
    {
        if(get(fieldName) != null)
        {
            return isMaxDate(getAsDate(fieldName));
        }
        return false;
    }

    public boolean isMaxDate(Date date)
    {
        return date.getTime() == SearchUtil.DATE_MAX_TIME_MILLIS;
    }
    
    /**
     * Returns the modification time of the resource represented by wrapped lucene document.
     * Resource modification time is stored in <code>modification_time</code> field.
     *
     * @return the object representing the modification time of the resource
	 * @see net.cyklotron.cms.search.SearchConstants#FIELD_MODIFICATION_TIME
     */
    public Date getModificationTime()
    throws Exception
    {
        return sh.getModificationTime();
    }

    /**
     * Returns hit score in promiles.
     *
     * @return the number representing promiles of search relevancy.
     */
    public int getScore()
    {
        return sh.getScore();
    }

    // public interface ////////////////////////////////////////////////////////////////////////////

    public void setNoAccess()
    {
        sh = new NoAccessSH();
    }
    
    void setUrl(String url)
    {
        this.url = url;
    }

    void setEditUrl(String url)
    {
        this.editUrl = url;
    }
    
    /**
     * Returns the id of the resource represented by wrapped lucene document.
     * Resource id is stored in <code>id</code> field.
     *
     * @return the id of the resource
	 * @see net.cyklotron.cms.search.SearchConstants#FIELD_ID
     */
    public long getId()
    {
        return Long.parseLong(doc.get(SearchConstants.FIELD_ID));
    }

    /**
     * Returns the id of the resource class of the resource represented by wrapped lucene document.
     * Resource class id is stored in <code>resource_class_id</code> field.
     *
     * @return the id of the resource class entity
	 * @see net.cyklotron.cms.search.SearchConstants#FIELD_RESOURCE_CLASS_ID
     */
    public long getResourceClassId()
    {
        return Long.parseLong(doc.get(SearchConstants.FIELD_RESOURCE_CLASS_ID));
    }
    
    // implementation //////////////////////////////////////////////////////////////////////////////

    public interface SH
    {
        public String get(String fieldName);
        public Date getModificationTime()
        throws Exception;
        public int getScore();
    }
    
    private class RealSH implements SH
    {
        private Document doc;
        private float score;

        public RealSH(Document doc, float score)
        {
            this.doc = doc;
            this.score = score;
        }

        public String get(String fieldName)
        {
            return doc.get(fieldName);
        }

        public Date getModificationTime()
        throws Exception
        {
            return SearchUtil.dateFromString(doc.get("modification_time"));
        }

        public int getScore()
        {
            return (int)(score*1000F);
        }
    }

    private class NoAccessSH implements SH
    {
        public String get(String fieldName)
        {
            return "NO_ACCESS";
        }

        public Date getModificationTime()
        {
            return new Date();
        }

        public int getScore()
        {
            return 0;
        }
    }
}
