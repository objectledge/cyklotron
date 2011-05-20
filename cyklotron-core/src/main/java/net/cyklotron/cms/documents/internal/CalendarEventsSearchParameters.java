package net.cyklotron.cms.documents.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.search.PoolResource;

public class CalendarEventsSearchParameters
{
    private int startDay;

    private int startMonth;

    private int startYear;

    private int endOffset;

    private Date startDate;

    private Date endDate;

    private String range;

    private String textQuery;

    private CategoryQueryResource categoryQuery;

    private Set<PoolResource> indexPools;

    private static Calendar calendar(Date date, Locale locale)
    {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        return calendar;
    }

    private static Calendar calendar(int year, int month, int day, Locale locale)
    {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.set(java.util.Calendar.YEAR, year);
        calendar.set(java.util.Calendar.MONTH, month - 1);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, day);
        return calendar;
    }

    private CalendarEventsSearchParameters(Calendar calendar, Set<PoolResource> indexPools)
    {
        this.startYear = calendar.get(java.util.Calendar.YEAR);
        this.startMonth = calendar.get(java.util.Calendar.MONTH) + 1;
        this.startDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        this.indexPools = indexPools;
    }

    private CalendarEventsSearchParameters(Calendar calendar, int offset,
        Set<PoolResource> indexPools)
    {
        this(calendar, indexPools);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        startDate = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        endDate = calendar.getTime();
    }

    /**
     * Create events search parameters.
     * <p>
     * startDate = 00:00:00 on startDay. <br/>
     * endDate = 23:59:59 on startDay + endOffset. <br/>
     * endOffset = 0 means one full day.
     * </p>
     */
    public CalendarEventsSearchParameters(Date startDay, int endOffset, Locale locale,
        Set<PoolResource> indexPools)
    {
        this(calendar(startDay, locale), endOffset, indexPools);
    }

    /**
     * Create events search parameters.
     * <p>
     * startDate = 00:00:00 on startDay. <br/>
     * endDate = 23:59:59 on startDay + endOffset. <br/>
     * endOffset = 0 means one full day.
     * </p>
     */
    public CalendarEventsSearchParameters(int startYear, int startMonth, int startDay,
        int endOffset, Locale locale, Set<PoolResource> indexPools)
    {
        this(calendar(startYear, startMonth, startDay, locale), endOffset, indexPools);
    }

    private CalendarEventsSearchParameters(Calendar calendar, String period,
        Set<PoolResource> indexPools)
    {
        this(calendar, indexPools);

        if(period.equals("daily"))
        {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            startDate = calendar.getTime();
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
            calendar.set(java.util.Calendar.MINUTE, 59);
            calendar.set(java.util.Calendar.SECOND, 59);
            endDate = calendar.getTime();
        }
        if(period.equals("monthly"))
        {
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            startDate = calendar.getTime();
            calendar.add(java.util.Calendar.MONTH, 1);
            calendar.add(java.util.Calendar.SECOND, -1);
            endDate = calendar.getTime();
        }
        if(period.equals("weekly"))
        {
            int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
            int firstDayOfWeek = calendar.getFirstDayOfWeek();
            if(dayOfWeek > firstDayOfWeek)
            {
                calendar.add(java.util.Calendar.DAY_OF_MONTH, -(dayOfWeek - firstDayOfWeek));
            }
            if(dayOfWeek < firstDayOfWeek)
            {
                calendar.add(java.util.Calendar.DAY_OF_MONTH, -(dayOfWeek + 7 - firstDayOfWeek));
            }
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            startDate = calendar.getTime();
            calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1);
            calendar.add(java.util.Calendar.SECOND, -1);
            endDate = calendar.getTime();
        }
    }

    /**
     * Create events search parameters.
     * <p>
     * period = "daily": startDate = 00:00:00 on date, endDate = 23:59:59 on date. <br>
     * period = "monthly": startDate = 00:00:00 on the first day of the month determined by date,
     * endDate = 23:59:59 on the last day of the month determined by date. <br/>
     * period = "weekly": starDate = 00:00:00 on the first day of the week determined by date,
     * endDate = 23:59:59 on the last day of the week determined by date. Note that first day of the
     * week may vary according to locale!
     * </p>
     */
    public CalendarEventsSearchParameters(Date date, String period, Locale locale,
        Set<PoolResource> indexPools)
    {
        this(calendar(date, locale), period, indexPools);
    }
    
    /**
     * Create events search parameters.
     * <p>
     * period = "daily": startDate = 00:00:00 on date, endDate = 23:59:59 on date. <br>
     * period = "monthly": startDate = 00:00:00 on the first day of the month determined by date,
     * endDate = 23:59:59 on the last day of the month determined by date. <br/>
     * period = "weekly": starDate = 00:00:00 on the first day of the week determined by date,
     * endDate = 23:59:59 on the last day of the week determined by date. Note that first day of the
     * week may vary according to locale!
     * </p>
     */
    public CalendarEventsSearchParameters(int startYear, int startMonth, int startDay,
        String period, Locale locale, Set<PoolResource> indexPools)
    {
        this(calendar(startYear, startMonth, startDay, locale), period, indexPools);
    }

    public void setTextQuery(String textQuery)
    {
        this.textQuery = textQuery;
    }

    public void setCategoryQuery(CategoryQueryResource categoryQuery)
    {
        this.categoryQuery = categoryQuery;
    }

    public int getStartDay()
    {
        return startDay;
    }

    public int getStartMonth()
    {
        return startMonth;
    }

    public int getStartYear()
    {
        return startYear;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public int getEndOffset()
    {
        return endOffset;
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

    public CategoryQueryResource getCategoryQuery()
    {
        return categoryQuery;
    }

    public Set<PoolResource> getIndexPools()
    {
        return indexPools;
    }
}
