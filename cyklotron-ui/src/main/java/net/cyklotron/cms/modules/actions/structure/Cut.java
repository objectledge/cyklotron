package net.cyklotron.cms.modules.actions.structure;

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
 * Cut action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Cut.java,v 1.3 2005-03-08 10:54:17 pablo Exp $
 */
public class Cut
    extends BaseCopyPasteAction
{
    public Cut(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long id = parameters.getLong("node_id");
        httpContext.setSessionAttribute(CLIPBOARD_MODE,"cut");
        httpContext.setSessionAttribute(CLIPBOARD_KEY,new Long(id));
    }
}
