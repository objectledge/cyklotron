package net.cyklotron.cms.modules.views.structure;

import java.util.Arrays;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 *
 */
public class DeleteNode
    extends BaseStructureScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode();

        Resource[] children = coralSession.getStore().getResource(node);
        templatingContext.put("children",Arrays.asList(children));
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return getCmsData().getNode().canRemove(coralSession.getUserSubject());
    }
}
