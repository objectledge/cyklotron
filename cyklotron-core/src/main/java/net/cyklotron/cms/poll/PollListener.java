package net.cyklotron.cms.poll;

import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.Labeo;
import net.labeo.services.resource.Resource;

/**
 * Poll Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PollListener.java,v 1.1 2005-01-12 20:45:01 pablo Exp $
 */
public class PollListener
extends BaseSiteListener
implements SiteCreationListener
{
    /** site service */
    private PollService pollService;

    protected synchronized void init()
    {
        if(!initialized)
        {
            pollService = (PollService)Labeo.getBroker().getService(PollService.SERVICE_NAME);
            super.init();
        }
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
        init();
        try
        {
            SiteResource site = siteService.getSite(name);
            PollsResource pollsRoot = pollService.getPollsRoot(site);
            Resource[] nodes = resourceService.getStore().getResource(site, "security");
            if(nodes.length != 1)
            {
                log.error("Security node for site couldn't be found");
            }
            cmsSecurityService.createRole(site.getAdministrator(), 
                 "cms.poll.polls.administrator", pollsRoot, rootSubject);
        }
        catch(Exception e)
        {
            log.error("PollListenerException: ",e);
        }
    }
}
