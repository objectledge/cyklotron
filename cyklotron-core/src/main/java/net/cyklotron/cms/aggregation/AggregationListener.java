package net.cyklotron.cms.aggregation;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

/**
 * Aggregation Listener implementation
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AggregationListener.java,v 1.3 2005-01-18 09:00:11 pablo Exp $
 */
public class AggregationListener 
    extends BaseSiteListener 
    implements SiteCreationListener
{
    public AggregationListener(Logger logger, CoralSessionFactory sessionFactory,
        SiteService siteService, SecurityService cmsSecurityService)
    {
        super(logger, sessionFactory, siteService, cmsSecurityService);
    }
    
    // listeners implementation ////////////////////////////////////////////////////////

    /**
     * Called when a new site is created.
     *
     * <p>The method will be called after the site Resources are successfully
     * copied from the template.</p>
     *
     * @param template the site template name.
     * @param name the site name.
     */
    public void createSite(String template, String name)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            SiteResource site = siteService.getSite(coralSession, name);
            Role administrator = site.getAdministrator();
            cmsSecurityService.createRole(coralSession, administrator, 
                "cms.aggregation.export.administrator", site);
            cmsSecurityService.createRole(coralSession, administrator, 
                "cms.aggregation.import.administrator", site);
        }
        catch(SiteException e)
        {
            log.error("Could not get site root for site '"+name+"' ",e);
        }
        catch(CmsSecurityException e)
        {
            log.error("Could not create the site '"+name+"' ",e);
        }
        finally
        {
            coralSession.close();
        }
    }
}
