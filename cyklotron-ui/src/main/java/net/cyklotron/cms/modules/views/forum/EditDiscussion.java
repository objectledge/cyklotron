package net.cyklotron.cms.modules.views.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 * The edit discussion screen class.
 */
public class EditDiscussion
    extends BaseForumScreen
{
    
    public EditDiscussion(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, ForumService forumService,
        WorkflowService workflowService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, forumService,
                        workflowService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long did = parameters.getLong("did", -1);
        if(did == -1)
        {
            throw new ProcessingException("Discussion id not found");
        }
        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession,did);
            templatingContext.put("discussion",discussion);
            templatingContext.put("transitions",workflowService.getTransitions(coralSession, discussion.getState()));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found",e);
        }
        catch(WorkflowException e)
        {
            throw new ProcessingException("WorkflowException",e);
        }
    }    
}
