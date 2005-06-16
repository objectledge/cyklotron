package net.cyklotron.cms.syndication;

import java.util.Date;

import com.sun.syndication.io.impl.DateParser;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FeedDateFormatter.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
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
        return DateParser.formatW3CDateTime(date);
    }
}
