package net.cyklotron.cms.periodicals;

import java.util.Date;
import java.util.List;

import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * Provides periodicals framework. 
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalsService.java,v 1.10 2006-05-16 09:47:43 rafal Exp $
 */
public interface PeriodicalsService
{
    // constants ////////////////////////////////////////////////////////////

    /** service name constant. */
    public static final String SERVICE_NAME = "cms_periodicals";
    
    /** the logging facility name. */
    public static final String LOGGING_FACILITY = "cms_periodicals";    
    
    // public API ///////////////////////////////////////////////////////////

    /**
     * List the periodicals existing in the site.
     * 
     * @param site the site.
     * @return array of periodicals.
     */
    public PeriodicalResource[] getPeriodicals(CoralSession coralSession, SiteResource site)
        throws PeriodicalsException;
    
	/**
	 * List the email periodicals existing in the site.
	 * 
	 * @param site the site.
	 * @return array of periodicals.
	 */
	public EmailPeriodicalResource[] getEmailPeriodicals(CoralSession coralSession, SiteResource site)
		throws PeriodicalsException;
    
    /**
     * Get root node of application's data.
     * 
     * @param coralSession CoralSession.
     * @param site the site.
     * @return root node of application data.
     * @throws PeriodicalsException
     */
    public PeriodicalsNodeResource getApplicationRoot(CoralSession coralSession, SiteResource site)
        throws PeriodicalsException;
    
	/**
	 * Return the root node for periodicals
	 * 
	 * @param site the site.
	 * @return the periodicals root.
	 */
	public PeriodicalsNodeResource getPeriodicalsRoot(CoralSession coralSession, SiteResource site) 
		throws PeriodicalsException;
		
	/**
	 * Return the root node for email periodicals
	 * 
	 * @param site the site.
	 * @return the periodicals root.
	 */
	public EmailPeriodicalsRootResource getEmailPeriodicalsRoot(CoralSession coralSession, SiteResource site)
		throws PeriodicalsException;
        
    /**
     * Returns an email address that should be used in From header in machine
     * generated messages, that have no other valid sender.
     * 
     * @return an e-mail address.
     */
    public String getFromAddress();
    
    // for debugging, mostly ////////////////////////////////////////////////
    
    /**
     * Publish the peridical immediately.
     * 
     * @parm periodical the periodical.
     * @param update should periodical's lastPublishedTime be updated?
     * @param send should the message be sent?
     * @param recipient message recipient override
     */
    public List<FileResource> publishNow(CoralSession coralSession, PeriodicalResource periodical,
        boolean update, boolean send, String recipient)
        throws PeriodicalsException;
    
    // renderers ////////////////////////////////////////////////////////////
    
    /**
     * Returns the names of the configured renderers.
     * 
     * @return the names of the configured renderers.
     */
    public String[] getRendererNames();

    /**
     * Returns an instance of the given renderer.
     * 
     * @param name name of the renderer.
     * @return an instance of the renderer.
     */
    public PeriodicalRenderer getRenderer(String name);
    
    /**
     * Releases an instance of the renderer after use.
     * 
     * @param renderer the rendeder.
     */
    public void releaseRenderer(PeriodicalRenderer renderer);

    // template variants ////////////////////////////////////////////////////

    /**
     * Returns an instance of the offilne link tool.
     * 
     * @return an instance of the offilne link tool.
     */
    public LinkRenderer getLinkRenderer(); 
    
    // scheduler entry point ////////////////////////////////////////////////
    
    /**
     * Process periodicals defined in all sites.
     * 
     * <p>This method is supposed to be called by a scheduled job, once each
     * hour.</p>
     * 
     * @param time the time 
     */
    public void processPeriodicals(CoralSession coralSession,Date time)
        throws PeriodicalsException;
}
