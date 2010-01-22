package net.cyklotron.cms.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.event.EventWhiteboard;
import org.picocontainer.Startable;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteDestructionValve;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Aggregation Listener implementation
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AggregationListener.java,v 1.6 2005-05-31 17:10:56 pablo Exp $
 */
public class AggregationListener 
    extends BaseSiteListener 
    implements SiteCreationListener, SiteDestructionValve, Startable
{
    public AggregationListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        eventWhiteboard.addListener(SiteCreationListener.class,this,null);
    }
    
    /**
     * {@inheritDoc}
     */
    public void start()
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
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
    public void createSite(SiteService siteService, String template, String name)
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
    
    /**
     * {@inheritDoc}
     */
    public void clearApplication(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "aggregation");
        if(res.length == 0)
        {
            return;
        }
        deleteSiteNode(coralSession, res[0]);
    }
    

    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        
    }
}
