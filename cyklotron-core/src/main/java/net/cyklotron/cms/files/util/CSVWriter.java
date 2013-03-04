package net.cyklotron.cms.files.util;

import java.io.IOException;
import java.io.Writer;

/**
 * @author rafal.rzewski@caltha.pl
 */
public class CSVWriter
    implements AutoCloseable
{
    private enum State
    {
        START, FIELD
    }

    private final char sep;

    private final char quote;

    private final String quoteStr;

    private final String dQuoteStr;

    private final Writer w;

    private final String nl;

    private CSVWriter.State state = State.START;

    public CSVWriter(Writer w, char sep, char quote, String nl)
    {
        this.w = w;
        this.sep = sep;
        this.quote = quote;
        this.nl = nl;
        this.quoteStr = new String(new char[] { quote });
        this.dQuoteStr = new String(new char[] { quote, quote });
    }

    public void field(String s)
        throws IOException
    {
        if(state == State.FIELD)
        {
            w.append(sep);
        }
        else
        {
            state = State.FIELD;
        }
        if(s.indexOf(sep) >= 0 || s.indexOf(quote) >= 0 || s.indexOf(nl) >= 0)
        {
            w.append(quote);
            w.append(s.replace(quoteStr, dQuoteStr));
            w.append(quote);
        }
        else
        {
            w.append(s);
        }
    }

    public void endRecord()
        throws IOException
    {
        w.append(nl);
        state = State.START;
    }

    public void flush()
        throws IOException
    {
        w.flush();
    }

    public void close()
        throws IOException
    {
        w.close();
    }
}