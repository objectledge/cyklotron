package net.cyklotron.cms.style;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.event.EventWhiteboard;
import org.objectledge.filesystem.FileSystem;
import org.picocontainer.Startable;

import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteDestructionValve;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public class StyleSiteDestructionListener
    extends BaseSiteListener
    implements SiteDestructionValve, Startable
{
    protected FileSystem fileSystem;
    
    protected StyleService styleService;

    public StyleSiteDestructionListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
        FileSystem fileSystem, StyleService styleService)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.fileSystem = fileSystem;
        this.styleService = styleService;
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
    
    /**
     * {@inheritDoc}
     */
    public void clearApplication(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource node = null;
        try
        {
            node = styleService.getStyleRoot(coralSession, site);
        }
        catch(Exception e)
        {
            // ignore it!
        }
        if(node != null)
        {
            deleteSiteNode(coralSession, node);
        }
        try
        {
            node = null;
            node = styleService.getLayoutRoot(coralSession, site);
        }
        catch(Exception e)
        {
            // ignore it!
        }
        if(node != null)
        {
            deleteSiteNode(coralSession, node);
        }
        Resource[] res = coralSession.getStore().getResource(site, "styles");
        if(res.length > 0)
        {
            deleteSiteNode(coralSession, res[0]);
        }
    }

    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        
    }
}

