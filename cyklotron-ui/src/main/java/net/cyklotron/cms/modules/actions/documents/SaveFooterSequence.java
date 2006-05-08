package net.cyklotron.cms.modules.actions.documents;


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
import net.cyklotron.cms.documents.FooterResource;
import net.cyklotron.cms.documents.FooterResourceImpl;
import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.modules.views.documents.FootersList;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SaveFooterSequence.java,v 1.1 2006-05-08 12:29:37 pablo Exp $
 */
public class SaveFooterSequence
    extends BaseStructureAction
{
    public SaveFooterSequence(Logger logger, StructureService structureService,
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
        
        List list = (List)httpContext.getSessionAttribute(FootersList.FOOTER_SEQUENCE);
        if(list == null)
        {
            throw new ProcessingException("Sequence list couldn't be found in session context");
        }
        try
        {
            for(int i = 0; i < list.size(); i++)
            {
                Long id = (Long)list.get(i);
                int sequence = i+1;
                FooterResource footer = FooterResourceImpl.getFooterResource(coralSession, id.longValue());
                int seq = footer.getSequence(-sequence);
                if(sequence != seq)
                {
                    footer.setSequence(sequence);
                    footer.update();
                }
            }
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
