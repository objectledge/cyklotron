/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.periodicals.internal;


/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PlainTextRenderer extends AbstractRenderer
{
    // inherit doc
    public String getFilenameSuffix()
    {
        return "txt";
    }

    // inherit doc
    public String getMimeType()
    {
        return "text/plain";
    }
    
    // inherit doc
    public String getMedium()
    {
        return "PLAIN";
    }
}
