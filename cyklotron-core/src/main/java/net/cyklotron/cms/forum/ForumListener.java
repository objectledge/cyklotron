package net.cyklotron.cms.forum;

import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.services.workflow.WorkflowService;
import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.resource.generic.NodeResourceImpl;

/**
 * Forum Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ForumListener.java,v 1.1 2005-01-12 20:45:07 pablo Exp $
 */
public class ForumListener
extends BaseSiteListener
implements SiteCreationListener
{
    /** workflow service */
    private WorkflowService workflowService;

    /** workflow service */
    private ForumService forumService;

    protected synchronized void init()
    {
        if(!initialized)
        {
            ServiceBroker broker = Labeo.getBroker();
            workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
            forumService = (ForumService)broker.getService(ForumService.SERVICE_NAME);
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
            // FIXME: MLM integration -- first subject in the followin call
            // should be the subject whose mailbox be used for mailing lists.
            ForumResource forum = forumService.createForum(site, site.getOwner(), site.getOwner());
            ForumNodeResource discussions = ForumNodeResourceImpl.
                createForumNodeResource(resourceService, "discussions", forum, site.getOwner());
            cmsSecurityService.createRole(forum.getAdministrator(),
                                          "cms.forum.administrator", discussions, rootSubject);
            ForumNodeResource comments = ForumNodeResourceImpl.
                createForumNodeResource(resourceService, "comments", forum, site.getOwner());
            cmsSecurityService.createRole(forum.getAdministrator(), 
                                          "cms.forum.administrator", comments, rootSubject);
        }
        catch(Exception e)
        {
            log.error("ForumListenerException: ", e);
        }
    }
}
