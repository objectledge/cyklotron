package net.cyklotron.cms.modules.views.structure;

import java.util.HashMap;
import java.util.List;

import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;


/**
 * The add post screen class
 */
public class EditNodeSequence
    extends BaseStructureScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode();

        Resource[] children = coralSession.getStore().getResource(node);
        HashMap childrenMap = new HashMap();
        for(int i = 0; i < children.length; i++)
        {
            childrenMap.put(children[i].getIdObject(), children[i]);
        }
        List childrenIds = (List)httpContext.getSessionAttribute(CURRENT_SEQUENCE);
        if(childrenIds == null)
        {
            throw new ProcessingException("Sequence list couldn't be found in session context");
        }
        templatingContext.put("childrenIds",childrenIds);
        templatingContext.put("childrenMap",childrenMap);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        try
        {
            NavigationNodeResource node = getNode();
            Permission permission = coralSession.getSecurity().
                getUniquePermission("cms.structure.move");
            return coralSession.getUserSubject().hasPermission(node, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }
}
