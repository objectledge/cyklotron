package net.cyklotron.cms.poll;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Poll Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollListener.java,v 1.3 2005-02-09 22:20:13 rafal Exp $
 */
public class PollListener
extends BaseSiteListener
implements SiteCreationListener
{
    /** site service */
    private PollService pollService;

    public PollListener(Logger logger, CoralSessionFactory sessionFactory,
        SiteService siteService, SecurityService cmsSecurityService, 
        PollService pollService)
    {
        super(logger, sessionFactory, siteService, cmsSecurityService);
        this.pollService = pollService;
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
    public void createSite(String template, String name)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            SiteResource site = siteService.getSite(coralSession,name);
            PollsResource pollsRoot = pollService.getPollsRoot(coralSession, site);
            Resource[] nodes = coralSession.getStore().getResource(site, "security");
            if(nodes.length != 1)
            {
                log.error("Security node for site couldn't be found");
            }
            cmsSecurityService.createRole(coralSession, site.getAdministrator(), 
                 "cms.poll.polls.administrator", pollsRoot);
        }
        catch(Exception e)
        {
            log.error("PollListenerException: ",e);
        }
        finally
        {
            coralSession.close();
        }
    }
}
