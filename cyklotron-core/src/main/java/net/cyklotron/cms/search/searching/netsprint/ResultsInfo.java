package net.cyklotron.cms.search.searching.netsprint;

import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ResultsInfo.java,v 1.1 2005-01-12 20:44:38 pablo Exp $
 */
public class ResultsInfo
{
    int documentsFound;
    String queryTime;
    String query;
    String oldQuery;
    int pageNumber;
    boolean hasPrevious;
    boolean hasNext;
    
    public ResultsInfo(Attributes attr)
    throws Exception
    {
        documentsFound = Integer.parseInt(attr.getValue("documents-found"));
        queryTime = attr.getValue("query-time");
        String pageNumValue = attr.getValue("pagenumber");
        if(pageNumValue != null && pageNumValue.length() > 0)
        {
			pageNumber = Integer.parseInt(pageNumValue);
        }
        else
        {
			pageNumber = 1;
        }
        String val = attr.getValue("has-previous");
        hasPrevious = (val != null && val.equals("true"));
        val = attr.getValue("has-next");
        hasNext = (val != null && val.equals("true"));
    }
    
    public int getDocumentsFound()
    {
        return documentsFound;
    }
    
    public boolean isHasNext()
    {
        return hasNext;
    }
    
    public boolean isHasPrevious()
    {
        return hasPrevious;
    }
    
    public String getOldQuery()
    {
        return oldQuery;
    }
    
    public int getPageNumber()
    {
        return pageNumber;
    }
    
    public String getQuery()
    {
        return query;
    }
    
    public String getQueryTime()
    {
        return queryTime;
    }

    void setOldQuery(String string)
    {
        oldQuery = string;
    }

    void setQuery(String string)
    {
        query = string;
    }
}
