package net.cyklotron.cms.modules.views.forum;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.forum.ForumConstants;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.WorkflowService;

import org.objectledge.pipeline.ProcessingException;


/**
 * The default void screen assember for e-forum application.
 */
public class BaseForumScreen
    extends BaseCMSScreen
    implements ForumConstants
{
    protected Logger log;

    protected ForumService forumService;

    protected WorkflowService workflowService;
    
    public BaseForumScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(CoralSession.LOGGING_FACILITY);
        forumService = (ForumService)broker.getService(ForumService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
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
            return coralSession.getUserSubject().hasRole(forumService.getForum(site).getAdministrator());
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
