package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;

public class UpdateConfiguration
    extends BaseCMSAction
{

    public UpdateConfiguration(Logger logger, StructureService structureService,
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
            long proposeDocumentNodeId = parameters.getLong("proposeDocumentNodeId", -1);
            NavigationNodeResource proposeDocumentNode = null;
            if(proposeDocumentNodeId != -1)
            {
                proposeDocumentNode = NavigationNodeResourceImpl.getNavigationNodeResource(
                    coralSession, proposeDocumentNodeId);
            }
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            structureService.setProposeDocumentNode(coralSession, cmsData.getSite(), proposeDocumentNode);
        }
        catch(Exception e)
        {
           throw new ProcessingException(e);
        }
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(cmsData.getSite().getAdministrator());
    }
}
