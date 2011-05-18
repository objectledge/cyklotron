package net.cyklotron.cms.documents.internal;

import java.util.Date;
import java.util.Set;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.search.PoolResource;

public class CalendarEventsSearchParameters
{
    private final Date startDate;

    private final Date endDate;

    private final String range;

    private final String textQuery;

    private final String textQueryField;

    private final CategoryQueryResource categoryQuery;
    
    private final Set<PoolResource> indexPools;

    public Set<PoolResource> getIndexPools()
    {
        return indexPools;
    }

    public CalendarEventsSearchParameters(Date startDate, Date endDate, String range, String textQuery,
        String textQueryField, CategoryQueryResource categoryQuery, Set<PoolResource> indexPools)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.range = range;
        this.textQuery = textQuery;
        this.textQueryField = textQueryField;
        this.categoryQuery = categoryQuery;
        this.indexPools = indexPools;
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