package net.cyklotron.cms.periodicals.internal;

import java.util.Calendar;
import java.util.Date;

import org.objectledge.utils.LedgeTestCase;

public class PeriodicalsServiceImplTest extends LedgeTestCase
{

    public PeriodicalsServiceImplTest()
    {
        super();
    }
    
    
    public void testLimitTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2005);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.DAY_OF_MONTH, 10);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        Date now = calendar.getTime();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int weekOffYear = calendar.get(Calendar.WEEK_OF_YEAR);
        
        System.out.println(weekDay + ":"+weekOffYear);
        
        
        Date expected = new Date();
        
        
        /// month test
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(9,11,-1, now));
        assertEquals(getTime(5,9,11), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(9,12,-1, now));
        assertEquals(getTime(5,9,12), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(9,13,-1, now));
        assertEquals(getTime(5,9,13), expected);
        
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(10,11,-1, now));
        assertEquals(getTime(5,10,11), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(10,12,-1, now));
        assertEquals(getTime(5,10,12), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(10,13,-1, now));
        assertEquals(getTime(4,10,13), expected);
        
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(11,11,-1, now));
        assertEquals(getTime(4,11,11), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(11,12,-1, now));
        assertEquals(getTime(4,11,12), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(11,13,-1, now));
        assertEquals(getTime(4,11,13), expected);
        
        
        ///// week test
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,11,5, now));
        assertEquals(getTime(5,9,11), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,12,5, now));
        assertEquals(getTime(5,9,12), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,13,5, now));
        assertEquals(getTime(5,9,13), expected);
        
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,11,6, now));
        assertEquals(getTime(5,10,11), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,12,6, now));
        assertEquals(getTime(5,10,12), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,13,6, now));
        assertEquals(getTime(5,3,13), expected);
        
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,11,7, now));
        assertEquals(getTime(5,4,11), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,12,7, now));
        assertEquals(getTime(5,4,12), expected);
        expected.setTime(PeriodicalsServiceImpl.getLimitTime(-1,13,7, now));
        assertEquals(getTime(5,4,13), expected);
        
    }

    
    private Date getTime(int month, int day, int hour)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, 2005);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
