package net.cyklotron.cms.modules.actions.structure;


import java.util.List;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SaveSequence.java,v 1.2 2005-01-24 10:26:59 pablo Exp $
 */
public class SaveSequence
    extends BaseStructureAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        List list = (List)httpContext.getSessionAttribute(CURRENT_SEQUENCE);
        if(list == null)
        {
            throw new ProcessingException("Sequence list couldn't be found in session context");
        }
        Subject subject = coralSession.getUserSubject();
        try
        {
            for(int i = 0; i < list.size(); i++)
            {
                Long id = (Long)list.get(i);
                NavigationNodeResource node =
                    NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, id.longValue());
                structureService.updateNodeSequence(node, i+1, subject);
            }
        }
        catch(StructureException e)
        {
            templatingContext.put("result","exception");
            log.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            log.error("ARLException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }

        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.structure.move");
    }
}
