/*
 * Created on Oct 24, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.periodicals;


/**
 * An utility class for rendering periodicals.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PeriodicalRendererFactory.java,v 1.1 2005-01-18 17:30:48 pablo Exp $ 
 */
public interface PeriodicalRendererFactory
{
    /**
     * 
     */
    public PeriodicalRenderer getRenderer();
    
    /**
     * Get the renderer class name
     */
    public String getRendererName();
}
