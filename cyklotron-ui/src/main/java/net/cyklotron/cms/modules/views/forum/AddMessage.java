package net.cyklotron.cms.modules.views.forum;

import java.util.StringTokenizer;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
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
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * The discussion list screen class.
 */
public class AddMessage
    extends BaseForumScreen
{
    
    public AddMessage(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        ForumService forumService, WorkflowService workflowService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, forumService,
                        workflowService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long did = parameters.getLong("did", -1);
        long mid = parameters.getLong("mid", -1);
        if(mid == -1 && did == -1)
        {
            throw new ProcessingException("Discussion id nor Message id not found");
        }
        try
        {
            if(did != -1)
            {
                DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession,did);
                templatingContext.put("discussion",discussion);
                templatingContext.put("parent",discussion);
            }
            else
            {
                MessageResource message = MessageResourceImpl.getMessageResource(coralSession,mid);
                DiscussionResource discussion = message.getDiscussion();
                templatingContext.put("parent_content",prepareContent(message.getContent()));
                templatingContext.put("discussion",discussion);
                templatingContext.put("parent",message);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found",e);
        }
    }    

    public boolean checkAccessRights(Context context)
    {
        return true;
    }

    private String prepareContent(String content)
    {
        StringBuilder sb = new StringBuilder("");
        StringTokenizer st = new StringTokenizer(content, "\n", false);
        while (st.hasMoreTokens()) {
            sb.append(">");
            sb.append(st.nextToken());
            sb.append("\n");
        }
        return sb.toString();
    }
}
