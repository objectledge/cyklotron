package net.cyklotron.cms.link;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.event.EventWhiteboard;
import org.picocontainer.Startable;

import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteDestructionValve;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Link Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: LinkListener.java,v 1.5 2005-05-31 17:12:22 pablo Exp $
 */
public class LinkListener
extends BaseSiteListener
implements SiteCreationListener, SiteDestructionValve, Startable
{
    /** link service */
    private LinkService linkService;

    public LinkListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
        LinkService linkService)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.linkService = linkService;
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

    //  --------------------       listeners implementation  ----------------------
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
            LinkRootResource linksRoot = linkService.getLinkRoot(coralSession, site);
            Resource[] nodes = coralSession.getStore().getResource(site, "security");
            if(nodes.length != 1)
            {
                log.error("Security node for site couldn't be found");
            }
            cmsSecurityService.createRole(coralSession, site.getAdministrator(), 
                "cms.links.administrator", linksRoot);
        }
        catch(Exception e)
        {
            log.error("LinkListenerException: ",e);
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
        Resource[] res = coralSession.getStore().getResource(site, "applications");
        if(res.length > 0)
        {
            Resource[] res2 = coralSession.getStore().getResource(res[0], "links");
            if(res2.length > 0)
            {
                // first clear pools than links 
                Resource[] res3 = coralSession.getStore().getResource(res2[0]);
                for(Resource r: res3)
                {
                    if(r instanceof PoolResource)
                    {
                        deleteSiteNode(coralSession, r);
                    }
                }
                deleteSiteNode(coralSession, res2[0]);
            }
        }    
    }
    
    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "applications");
        if(res.length > 0)
        {
            res = coralSession.getStore().getResource(res[0], "links");
            if(res.length > 0)
            {
                LinkRootResource root = (LinkRootResource)res[0];
                root.setAdministrator(null);
                root.update();
            }
        }    
    }
}
