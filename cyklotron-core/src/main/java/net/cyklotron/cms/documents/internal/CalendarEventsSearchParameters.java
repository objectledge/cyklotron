package net.cyklotron.cms.documents.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.search.PoolResource;

public class CalendarEventsSearchParameters
{
    private Date startDate;

    private Date endDate;

    private String range;

    private String textQuery;

    private String textQueryField;

    private CategoryQueryResource categoryQuery;
    
    private Set<PoolResource> indexPools;

    public Set<PoolResource> getIndexPools()
    {
        return indexPools;
    }

    public CalendarEventsSearchParameters(int year, int month, int day, int offset, Date now, Locale locale, Set<PoolResource> indexPools)
    {
        Calendar calendar = Calendar.getInstance(locale);
        
        calendar.setTime(now);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, day);
        calendar.set(java.util.Calendar.MONTH, month - 1);
        calendar.set(java.util.Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);        
        startDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        endDate = calendar.getTime();
        
        this.indexPools = indexPools;
    }
        
    public void setTextQuery(String textQuery, String textQueryField)
    {
        this.textQuery = textQuery;
        this.textQueryField = textQueryField;
    }
    
    public void setCategoryQuery(CategoryQueryResource categoryQuery)
    {
        this.categoryQuery = categoryQuery;
    }
    
    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public String getRange()
    {
        return range;
    }

    public String getTextQuery()
    {
        return textQuery;
    }

    public String getTextQueryField()
    {
        return textQueryField;
    }

    public CategoryQueryResource getCategoryQuery()
    {
        return categoryQuery;
    }
}