package net.cyklotron.cms.syndication;

import java.util.Date;

import com.sun.syndication.io.impl.DateParser;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedDateFormatter.java,v 1.2 2006-02-09 12:08:57 pablo Exp $
 */
public class FeedDateFormatter
{
    /**
     * Create a RFC822 representation of a date.
     * @param date Date to format.
     * @return the RFC822 represented by the given Date
     *         It returns <b>null</b> if it was not possible to format the date.
     */
    public static String formatRFC822(Date date)
    {
        if(date == null)
        {
            return null;
        }
        return DateParser.formatRFC822(date);
    }

    /**
     * Create a W3C Date Time representation of a date.
     * @param date Date to format.
     * @return the W3C Date Time represented by the given Date
     *         It returns <b>null</b> if it was not possible to format the date.
     *
     */
    public static String formatW3CDateTime(Date date)
    {
        if(date == null)
        {
            return null;
        }
        return DateParser.formatW3CDateTime(date);
    }
}
