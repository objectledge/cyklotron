package net.cyklotron.cms.modules.views.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * The discussion list screen class.
 */
public class AddDiscussion
    extends BaseForumScreen
{
    
    public AddDiscussion(org.objectledge.context.Context context, Logger logger,
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
        // nothing to do
    }    
}
