package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteNode.java,v 1.4 2005-03-08 10:54:17 pablo Exp $
 */
public class DeleteNode
    extends BaseStructureAction
{
    public DeleteNode(Logger logger, StructureService structureService,
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
        
        Subject subject = coralSession.getUserSubject();

        NavigationNodeResource node = getNode(context);

        // move to parent node
        Resource parentNode = node.getParent();
        long newNodeId = -1;
        if(parentNode instanceof NavigationNodeResource)
        {
            newNodeId = parentNode.getId();
        }
        parameters.set("node_id", newNodeId);
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        cmsDataFactory.removeCmsData(context);

        // delete current node
        try
        {
            structureService.deleteNode(coralSession, node, subject);
        }
        catch(StructureException e)
        {
            templatingContext.put("result","exception");
            logger.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return getCmsData(context).getNode().canRemove(context,coralSession.getUserSubject());
    }
}
