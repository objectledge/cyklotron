package net.cyklotron.cms.modules.actions.structure;

import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ChangeSequence.java,v 1.4 2005-03-08 10:54:17 pablo Exp $
 */
public class ChangeSequence
    extends BaseStructureAction
{
    public ChangeSequence(Logger logger, StructureService structureService,
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

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.structure.move");
    }
}
