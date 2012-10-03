package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class UnbindThumbnail
    extends BaseStructureAction
{

    public UnbindThumbnail(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, RelatedService relatedService,
        IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long resId = parameters.getLong("res_id", -1);
            NavigationNodeResource node = null;
            if(resId != -1)
            {
                node = DocumentNodeResourceImpl.getNavigationNodeResource(coralSession, resId);
            }
            node.setThumbnail(null);
            node.update();
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("internal error", e);
        }
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        return checkModifyPermission(context);
    }
}
