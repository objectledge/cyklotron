package net.cyklotron.cms.banner;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.site.SiteResource;
/**
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerService.java,v 1.4 2005-02-09 22:20:49 rafal Exp $
 */
public interface BannerService
{
    /** The name of the service (<code>"banner"</code>). */
    public final static String SERVICE_NAME = "banner";

    /**
     * The logging facility where the service issues it's informational
     * messages.
     */
    public static final String LOGGING_FACILITY = "banner";

	/**
	 * The logging facility where the clicks are logged.
	 */
	public static final String CLICK_LOGGING_FACILITY = "banner_clicks";

	/**
     * The log on click switch.
	 */
	public static final String LOG_ON_CLICK = "log_on_click";
	
	/**
	 * The update on click switch.
	 */
	public static final String UPDATE_ON_CLICK = "update_on_click";
	
    /**
     * return the banner root node.
     *
     * @param site the site resource.
     * @return the banners root resource.
     * @throws BannerException if the operation fails.
     */
    public BannersResource getBannersRoot(CoralSession coralSession, SiteResource site)
        throws BannerException;

    /**
     * return the next banner from banners root.
     *
     * @param root the banner root.
     * @param config the configuration.
     * @return the banner.
     */
    public BannerResource getBanner(CoralSession coralSession,BannersResource root, Parameters config)
        throws BannerException;
    
    /**
     * notify that banner was clicked.
     *
     * @param banner the banner that is being clicked.
     */
    public void followBanner(CoralSession coralSession,BannerResource banner);

    /**
     * delete the banner.
     *
     * @param banner the banner.
     */
    public void deleteBanner(CoralSession coralSession, BannerResource banner)
        throws BannerException;


    /**
     * execute logic of the job to check expiration date.
     */
    public void checkBannerState(CoralSession coralSession);
    
}
