/*
 * Created on Oct 27, 2003
 */
package net.cyklotron.cms.periodicals.internal;


/**
 * HTML Document renderer for periodicals.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLRenderer.java,v 1.1 2005-01-12 20:44:44 pablo Exp $
 */
public class HTMLRenderer extends AbstractRenderer
{
    // inherit doc
    public String getFilenameSuffix()
    {
        return "html";
    }

    // inherit doc
    public String getMimeType()
    {
        return "text/html";
    }
    
    // inherit doc
    public String getMedium()
    {
        return "HTML";
    }
}
