package net.cyklotron.cms.modules.actions.structure;


import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SaveSequence.java,v 1.3 2005-01-25 08:24:46 pablo Exp $
 */
public class SaveSequence
    extends BaseStructureAction
{
    public SaveSequence(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
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
                structureService.updateNodeSequence(coralSession, node, i+1, subject);
            }
        }
        catch(StructureException e)
        {
            templatingContext.put("result","exception");
            logger.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            logger.error("ARLException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }

        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.structure.move");
    }
}
