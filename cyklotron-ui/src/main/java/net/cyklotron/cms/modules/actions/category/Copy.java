package net.cyklotron.cms.modules.actions.category;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Copy.java,v 1.2 2005-01-24 10:27:04 pablo Exp $
 */
public class Copy
    extends BaseCopyPasteAction
{
    
    
    public Copy(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        CategoryService categoryService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, categoryService, integrationService);
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long id = parameters.getLong("cat_id");
        httpContext.setSessionAttribute(CLIPBOARD_MODE,"copy");
        httpContext.setSessionAttribute(CLIPBOARD_KEY,new Long(id));
    }
}
