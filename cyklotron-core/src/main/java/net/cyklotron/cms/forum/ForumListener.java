package net.cyklotron.cms.forum;

import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.workflow.WorkflowService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

/**
 * Forum Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ForumListener.java,v 1.3 2005-01-18 10:37:45 pablo Exp $
 */
public class ForumListener
extends BaseSiteListener
implements SiteCreationListener
{
    /** workflow service */
    private WorkflowService workflowService;

    /** workflow service */
    private ForumService forumService;

    public ForumListener(Logger logger, CoralSessionFactory sessionFactory,
        SiteService siteService, SecurityService cmsSecurityService, 
        ForumService forumService, WorkflowService workflowService)
    {
        super(logger, sessionFactory, siteService, cmsSecurityService);
        this.forumService = forumService;
        this.workflowService = workflowService;
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
            SiteResource site = siteService.getSite(coralSession, name);
            // FIXME: MLM integration -- first subject in the followin call
            // should be the subject whose mailbox be used for mailing lists.
            ForumResource forum = forumService.createForum(coralSession, site, site.getOwner());
            ForumNodeResource discussions = ForumNodeResourceImpl.
                createForumNodeResource(coralSession, "discussions", forum);
            cmsSecurityService.createRole(coralSession, forum.getAdministrator(),
                                          "cms.forum.administrator", discussions);
            ForumNodeResource comments = ForumNodeResourceImpl.
                createForumNodeResource(coralSession, "comments", forum);
            cmsSecurityService.createRole(coralSession, forum.getAdministrator(), 
                                          "cms.forum.administrator", comments);
        }
        catch(Exception e)
        {
            log.error("ForumListenerException: ", e);
        }
        finally
        {
            coralSession.close();
        }
    }
}
