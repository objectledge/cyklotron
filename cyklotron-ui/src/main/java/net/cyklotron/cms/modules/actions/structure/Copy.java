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
 * Copy action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Copy.java,v 1.2 2005-01-25 08:24:46 pablo Exp $
 */
public class Copy
    extends BaseCopyPasteAction
{
    public Copy(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        StyleService styleService)
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
        long id = parameters.getLong("node_id");
        httpContext.setSessionAttribute(CLIPBOARD_MODE,"copy");
        httpContext.setSessionAttribute(CLIPBOARD_KEY,new Long(id));
    }
}
