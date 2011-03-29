package net.cyklotron.cms.modules.actions.related;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

public class UpdateOrder
    extends BaseRelatedAction
{
    private final CoralSessionFactory coralSessionFactory;

    public UpdateOrder(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, RelatedService relatedService, CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, relatedService);
        this.coralSessionFactory = coralSessionFactory;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long resId = parameters.getLong("res_id", -1L);
            long[] sequence = parameters.getLongs("listItem[]");
            
            ResourceList<Resource> sequenceList = new ResourceList<Resource>(coralSessionFactory);
                        
            for(int i=0;i<sequence.length;i++)
            {
                sequenceList.add(i, sequence[i]);
            }
            
            DocumentNodeResource node = (DocumentNodeResource)coralSession.getStore().getResource(resId);
            
            node.setRelatedResourcesSequence(sequenceList);
            node.update();
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException(e);
        }
    }

}
