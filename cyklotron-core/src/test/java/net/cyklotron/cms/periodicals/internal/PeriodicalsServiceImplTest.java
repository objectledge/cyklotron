package net.cyklotron.cms.periodicals.internal;

import static net.cyklotron.cms.periodicals.internal.PeriodicalsServiceImpl.getScheduledPublicationTimeBefore;

import java.util.Calendar;
import java.util.Date;

import org.objectledge.test.LedgeTestCase;

public class PeriodicalsServiceImplTest
    extends LedgeTestCase
{

    public PeriodicalsServiceImplTest()
    {
        super();
    }

    public void testLimitTime()
    {
        Date now = time(2005, 5, 10, 12);
        Date actual = new Date();

        // // // month test
        actual.setTime(getScheduledPublicationTimeBefore(9, -1, 11, now));
        assertEquals(time(2005, 5, 9, 11), actual);
        actual.setTime(getScheduledPublicationTimeBefore(9, -1, 12, now));
        assertEquals(time(2005, 5, 9, 12), actual);
        actual.setTime(getScheduledPublicationTimeBefore(9, -1, 13, now));
        assertEquals(time(2005, 5, 9, 13), actual);

        actual.setTime(getScheduledPublicationTimeBefore(10, -1, 11, now));
        assertEquals(time(2005, 5, 10, 11), actual);
        actual.setTime(getScheduledPublicationTimeBefore(10, -1, 12, now));
        assertEquals(time(2005, 5, 10, 12), actual);
        actual.setTime(getScheduledPublicationTimeBefore(10, -1, 13, now));
        assertEquals(time(2005, 4, 10, 13), actual);

        actual.setTime(getScheduledPublicationTimeBefore(11, -1, 11, now));
        assertEquals(time(2005, 4, 11, 11), actual);
        actual.setTime(getScheduledPublicationTimeBefore(11, -1, 12, now));
        assertEquals(time(2005, 4, 11, 12), actual);
        actual.setTime(getScheduledPublicationTimeBefore(11, -1, 13, now));
        assertEquals(time(2005, 4, 11, 13), actual);

        // // // week test
        actual.setTime(getScheduledPublicationTimeBefore(-1, 5, 11, now));
        assertEquals(time(2005, 5, 9, 11), actual);
        actual.setTime(getScheduledPublicationTimeBefore(-1, 5, 12, now));
        assertEquals(time(2005, 5, 9, 12), actual);
        actual.setTime(getScheduledPublicationTimeBefore(-1, 5, 13, now));
        assertEquals(time(2005, 5, 9, 13), actual);

        actual.setTime(getScheduledPublicationTimeBefore(-1, 6, 11, now));
        assertEquals(time(2005, 5, 10, 11), actual);
        actual.setTime(getScheduledPublicationTimeBefore(-1, 6, 12, now));
        assertEquals(time(2005, 5, 10, 12), actual);
        actual.setTime(getScheduledPublicationTimeBefore(-1, 6, 13, now));
        assertEquals(time(2005, 5, 3, 13), actual);

        actual.setTime(getScheduledPublicationTimeBefore(-1, 7, 11, now));
        assertEquals(time(2005, 5, 4, 11), actual);
        actual.setTime(getScheduledPublicationTimeBefore(-1, 7, 12, now));
        assertEquals(time(2005, 5, 4, 12), actual);
        actual.setTime(getScheduledPublicationTimeBefore(-1, 7, 13, now));
        assertEquals(time(2005, 5, 4, 13), actual);
    }

    private Date time(int year, int month, int day, int hour)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
