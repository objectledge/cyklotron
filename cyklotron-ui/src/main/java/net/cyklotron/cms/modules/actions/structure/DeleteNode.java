package net.cyklotron.cms.modules.actions.structure;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteNode.java,v 1.1 2005-01-24 04:33:55 pablo Exp $
 */
public class DeleteNode
    extends BaseStructureAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
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
        CmsData.removeCmsData(data);

        // delete current node
        try
        {
            structureService.deleteNode(node, subject);
        }
        catch(StructureException e)
        {
            templatingContext.put("result","exception");
            log.error("StructureException: ",e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return getCmsData(context).getNode(context).canRemove(coralSession.getUserSubject());
    }
}
