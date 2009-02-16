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
 * @version $Id: PeriodicalRendererFactory.java,v 1.4 2005-03-08 10:49:53 pablo Exp $ 
 */
public interface PeriodicalRendererFactory
{
    /**
     * @param periodicalsService the periodical service
     */
    public PeriodicalRenderer getRenderer(PeriodicalsService periodicalsService);
    
    /**
     * Get the renderer class name
     */
    public String getRendererName();
}
