package net.cyklotron.cms.modules.views.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumConstants;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 * The default void screen assember for e-forum application.
 */
public abstract class BaseForumScreen
    extends BaseCMSScreen
    implements ForumConstants
{
    protected ForumService forumService;

    protected WorkflowService workflowService;

    
    public BaseForumScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        ForumService forumService, WorkflowService workflowService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.forumService = forumService;
        this.workflowService = workflowService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        try
        {
            SiteResource site = cmsData.getSite();
            if(site == null)
            {
                site = cmsData.getGlobalComponentsDataSite();
            }
            if(site == null)
            {
                throw new ProcessingException("No site selected");
            }
            return coralSession.getUserSubject().hasRole(forumService.getForum(coralSession,site).getAdministrator());
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            throw new ProcessingException("failed to check access privileges", e);
        }
    }

}
