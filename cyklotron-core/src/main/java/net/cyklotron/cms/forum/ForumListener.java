package net.cyklotron.cms.forum;

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
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * Forum Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ForumListener.java,v 1.7 2005-05-31 17:10:19 pablo Exp $
 */
public class ForumListener
extends BaseSiteListener
implements SiteCreationListener, SiteDestructionValve, Startable
{
    /** workflow service */
    private WorkflowService workflowService;

    /** workflow service */
    private ForumService forumService;

    public ForumListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
        ForumService forumService, WorkflowService workflowService)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.forumService = forumService;
        this.workflowService = workflowService;
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
            // MLM integration -- first subject in the followin call
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
    
    /**
     * {@inheritDoc}
     */
    public void clearApplication(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "applications");
        if(res.length > 0)
        {
            res = coralSession.getStore().getResource(res[0], "forum");
            if(res.length > 0)
            {
                deleteSiteNode(coralSession, res[0]);
            }
        }    
    }
    

    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "applications");
        if(res.length > 0)
        {
            res = coralSession.getStore().getResource(res[0], "forum");
            if(res.length > 0)
            {
                ForumNodeResource node = (ForumNodeResource)res[0];
                node.setAdministrator(null);
                node.setModerator(null);
                node.setParticipant(null);
                node.setVisitor(null);
                node.update();
            }
        }    
    }
}
