package net.cyklotron.cms.modules.views.forum;

import java.util.StringTokenizer;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;

/**
 * The discussion list screen class.
 */
public class AddMessage
    extends BaseForumScreen
    implements Secure
{
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
        StringBuffer sb = new StringBuffer("");
        StringTokenizer st = new StringTokenizer(content, "\n", false);
        while (st.hasMoreTokens()) {
            sb.append(">");
            sb.append(st.nextToken());
            sb.append("\n");
        }
        return sb.toString();
    }
}
