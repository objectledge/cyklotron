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
 * @version $Id: PeriodicalRendererFactory.java,v 1.2 2005-02-02 23:08:05 pablo Exp $ 
 */
public interface PeriodicalRendererFactory
{
    /**
     * @param periodicalService TODO
     * 
     */
    public PeriodicalRenderer getRenderer(PeriodicalsService periodicalsService);
    
    /**
     * Get the renderer class name
     */
    public String getRendererName();
}
