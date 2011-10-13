package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteNodes.java,v 1.3 2008-01-03 20:13:41 rafal Exp $
 */
public class DeleteNodes
    extends BaseStructureAction
{
    public DeleteNodes(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Subject subject = coralSession.getUserSubject();
            long[] ids = parameters.getLongs("op_node_id");
            Long clipId = (Long)httpContext.getSessionAttribute(CLIPBOARD_KEY);
            for(long id: ids)
            {
                NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, id);
                if(node.canRemove(coralSession, coralSession.getUserSubject()) &&
                    node.getChildren().length == 0)
                {
                    if(clipId != null && node.getId() == clipId.longValue())
                    {
                        httpContext.removeSessionAttribute(CLIPBOARD_MODE);
                        httpContext.removeSessionAttribute(CLIPBOARD_KEY);
                        clipId = null;
                    }
                    structureService.deleteNode(coralSession, node, subject);
                }
            }
            templatingContext.put("result","deleted_successfully");
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to delete selected nodes", e);
        }
    }

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            long[] nodeIds = parameters.getLongs("op_node_id");
            for(long nodeId : nodeIds)
            {
                NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
                Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.delete");
                if(!coralSession.getUserSubject().hasPermission(node, permission))
                {
                    return false;
                }
            }
            return true;
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during access rights checking",e);
        }
    }
}
