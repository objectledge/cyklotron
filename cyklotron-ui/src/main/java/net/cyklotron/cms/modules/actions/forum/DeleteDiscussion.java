package net.cyklotron.cms.modules.actions.forum;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.DiscussionResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteDiscussion.java,v 1.2 2005-01-24 10:27:03 pablo Exp $
 */
public class DeleteDiscussion
    extends BaseForumAction
    implements Secure
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        long discussionId = parameters.getLong("did", -1);
        if (discussionId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession, discussionId);
            Resource[] children = coralSession.getStore().getResource(discussion);
            if(children.length > 0)
            {
                templatingContext.put("result","discussion_in_use");
                return;
            }
            coralSession.getStore().deleteResource(discussion);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("ResourceException: ",e);
            return;
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("ResourceException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }


    public boolean checkAccessRights(Context context)
    {
        long discussionId = parameters.getLong("did", -1);
        if (discussionId == -1)
        {
            return true;
        }
        try
        {
            DiscussionResource discussion = DiscussionResourceImpl.getDiscussionResource(coralSession, discussionId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.forum.delete");
            return coralSession.getUserSubject().hasPermission(discussion, permission);
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to delete this discussion" , e);
            return false;
        }    
    }    

}


