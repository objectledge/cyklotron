package net.cyklotron.cms.modules.actions.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.forum.MessageResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteMessage.java,v 1.3 2005-01-25 03:21:37 pablo Exp $
 */
public class DeleteMessage
    extends BaseForumAction
{
    
    public DeleteMessage(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, ForumService forumService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, forumService, workflowService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long messageId = parameters.getLong("mid", -1);
        if (messageId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        try
        {
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession, messageId);
            coralSession.getStore().deleteResource(message);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("ResourceException: ",e);
            return;
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("ResourceException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
    
    public boolean checkAccessRights(Context context)
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);

        long messageId = parameters.getLong("mid", -1);
        if (messageId == -1)
        {
            return true;
        }
        try
        {
            MessageResource message = MessageResourceImpl.getMessageResource(coralSession, messageId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.forum.delete");
            return coralSession.getUserSubject().hasPermission(message, permission);
        }
        catch(Exception e)
        {
            logger.error("Subject has no rights to delete this message" , e);
            return false;
        }    
    }    

}


