package net.cyklotron.cms.modules.actions.structure;

import java.util.List;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ChangeSequence.java,v 1.1 2005-01-24 04:33:55 pablo Exp $
 */
public class ChangeSequence
    extends BaseStructureAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        int direction = parameters.getInt("dir", 0);
        long child_id = parameters.getLong("child_id", -1);
        if (child_id == -1 || direction == 0)
        {
            throw new ProcessingException("child id or dir parameter could not be found");
        }
        List childrenIds = (List)httpContext.getSessionAttribute(CURRENT_SEQUENCE);
        if (childrenIds == null)
        {
            throw new ProcessingException("Sequence list couldn't be found in session context");
        }
        int i = 0;
        for(; i < childrenIds.size(); i++)
        {
            if(child_id == ((Long)childrenIds.get(i)).longValue())
            {
                break;
            }
        }
        Object id = childrenIds.remove(i);
        switch (direction)
        {
        case -1:
            childrenIds.add(i-1,id);
            break;
        case 1:
            childrenIds.add(i+1,id);
            break;
        case -2:
            childrenIds.add(0,id);
            break;
        case 2:
            childrenIds.add(id);
            break;
        default:
            throw new ProcessingException("Unsupported direction");
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.structure.move");
    }
}
