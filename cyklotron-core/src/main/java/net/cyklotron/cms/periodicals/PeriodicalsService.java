package net.cyklotron.cms.periodicals;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.cyklotron.cms.documents.LinkRenderer;
import net.cyklotron.cms.site.SiteResource;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;

/**
 * Provides periodicals framework. 
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PeriodicalsService.java,v 1.3 2005-02-09 19:22:23 rafal Exp $
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
     * Create a subscription change request.
     * 
     * @param email the requestors email address.
     * @return a magic cookie to be returned to the user.
     */
    public String createSubsriptionRequest(CoralSession coralSession,SiteResource site, String email, String items)
        throws PeriodicalsException;
    
    /**
     * Return a subscription change request.
     * 
     * @param cookie the magic cookie recieved form the user.
     * @return the request object, or null if invalid.
     */
    public SubscriptionRequestResource getSubscriptionRequest(CoralSession coralSession,String cookie)
        throws PeriodicalsException;
    
    /**
     * Discard a subscription change request.
     * 
     * @param cookie the magic cookie recieved form the user.
     */
    public void discardSubscriptionRequest(CoralSession coralSession, String cookie)
        throws PeriodicalsException;
    
    /**
     * Returns periodicals in the given site, the address is subscribed to.
     * 
     * @param site the site 
     * @param email the email address
     * @return an array of email periodical resources.
     * @throws PeriodicalsException
     */
    public EmailPeriodicalResource[] getSubscribedEmailPeriodicals(CoralSession coralSession,SiteResource site, String email)
        throws PeriodicalsException;
    
    // mail from address ////////////////////////////////////////////////////
    
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
     */
    public void publishNow(CoralSession coralSession, PeriodicalResource periodical)
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
     * Returns the names of the template variants defined for a specific renderer.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @return the names of the template variants defined for a specific renderer.
     */    
    public String[] getTemplateVariants(SiteResource site, String renderer)
        throws PeriodicalsException;
    
    /**
     * Checks if the specified variant of the renderer's template exists in the site.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the varaint's name.
     * @return <code>true</code> if the specified variant of the renderer's template 
     *         exists in the site.
     */
    public boolean hasTemplateVariant(SiteResource site, String renderer, String name);

    /**
     * Returns a specific renderer's template variant.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * 
     * @return the variant's template.
     */    
    public Template getTemplateVariant(SiteResource site, String renderer, String name)
        throws TemplateNotFoundException, PeriodicalsException;
    
    /**
     * Creates a new variant of the renderer's template.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @param contents the template contents.
     */
    public void createTemplateVariant(SiteResource site, String renderer, String name, String contents)
        throws ProcessingException;
    
    /**
     * Deletes a renderer's template variant.
     *
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     */
    public void deleteTemplateVariant(SiteResource site, String renderer, String name)
        throws ProcessingException;

    /**
     * Returns a list of Loacales in which the application provides a template
     * for the specified renderer.
     *  
     * @param renderer the renderer
     * @return a list of Locale objects.
     */        
    public List getDefaultTemplateLocales(String renderer)
        throws ProcessingException;

    /**
     * Returns the contetns of the application provided template.
     * 
     * @param renderer the renderer.
     * @param locale the locale.
     * @return the contents of the template.
     */        
    public String getDefaultTemplateContents(String renderer, Locale locale)
        throws ProcessingException;

    /**
     * Returns the application provided template.
     * 
     * @param renderer the renderer.
     * @param locale the locale.
     * @return the template, or <code>null</code> if not avaialable.
     */        
    public Template getDefaultTemplate(String renderer, Locale locale)
        throws ProcessingException;

    /**
     * Returns the contents of the renderer's template variant as a String.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @return the contents of the renderer's template variant as a String.
     */
    public String getTemplateVariantContents(SiteResource site, String renderer, String name)
        throws ProcessingException;
    
    /**
     * Writes the contnets of the renderer's template variant into a provided OutputStream.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @param out the stream to write contents to.
     */
    public void getTemplateVariantContents(SiteResource site, String renderer, String name, OutputStream out)
        throws ProcessingException;
    
    /**
     * Returns the length (byte count) of the renderer's template contents.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @return the length (byte count) of the renderer's template contents.
     */
    public long getTemplateVariantLength(SiteResource site, String renderer, String name)
        throws ProcessingException;
    
    /**
     * Sets the contents of a renderer's template variant.
     * 
     * @param site the site.
     * @param renderer the renderer.
     * @param name the variant's name.
     * @param contents the contents of the template.
     */
    public void setTemplateVariantContents(SiteResource site, String renderer, String name, String contents)
        throws ProcessingException;
    
    // offline link tool access /////////////////////////////////////////////
    
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
