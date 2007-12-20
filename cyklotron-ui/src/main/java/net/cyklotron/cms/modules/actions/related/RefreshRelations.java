package net.cyklotron.cms.modules.actions.related;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.StructureService;

public class RefreshRelations
    extends BaseRelatedAction
{

    public RefreshRelations(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, relatedService);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long resId = parameters.getLong("res_id", -1L);
        try
        {
            Resource resource = coralSession.getStore().getResource(resId);
            ResourceSelectionState relatedState = ResourceSelectionState.getState(context,
                RELATED_SELECTION_STATE);
            relatedState.update(parameters);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException(e);
        }
    }
}
