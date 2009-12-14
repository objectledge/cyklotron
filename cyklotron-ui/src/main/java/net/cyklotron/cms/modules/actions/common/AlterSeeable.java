package net.cyklotron.cms.modules.actions.common;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.SeeableResource;
import net.cyklotron.cms.SeeableResourceImpl;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

/**
 * An action to alter 'hidden' property of 'seeable' resources
 * 
 *  @author rafal
 */
public class AlterSeeable
    extends BaseCMSAction
{
    public AlterSeeable(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory)
    {
        super(logger, structureService, cmsDataFactory);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {            
            long resId = parameters.getLong("res_id");
            SeeableResource res = SeeableResourceImpl.getSeeableResource(coralSession, resId);
            boolean requested = parameters.getBoolean("hidden");
            res.setHidden(requested);
            res.update();
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }
}
