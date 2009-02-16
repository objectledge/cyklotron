package net.cyklotron.cms.skins;

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

public class SkinsSiteDestructionListener
    extends BaseSiteListener
    implements SiteDestructionValve, Startable
{
    protected FileSystem fileSystem;
    
    protected SkinService skinService;

    public SkinsSiteDestructionListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
        FileSystem fileSystem, SkinService skinService)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.fileSystem = fileSystem;
        this.skinService = skinService;
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
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length > 0)
        {
            deleteSiteNode(coralSession, res[0]);
        }
        String dst = "/templates/sites/"+site.getName();
        if(fileSystem.exists(dst))
        {
            fileSystem.deleteRecursive(dst);
        }
        dst = "/content/sites/"+site.getName();
        if(fileSystem.exists(dst))
        {
            fileSystem.deleteRecursive(dst);
        }
    }
    
    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        
    }
}
