package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.html.HTMLService;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;
import net.cyklotron.cms.style.StyleService;

public class UpdateRemovalRequest
    extends BaseStructureAction
{
    private final CategoryService categoryService;

    private final RelatedService relatedService;

    private final HTMLService htmlService;

    public UpdateRemovalRequest(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, CategoryService categoryService,
        RelatedService relatedService, HTMLService htmlService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.categoryService = categoryService;
        this.relatedService = relatedService;
        this.htmlService = htmlService;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        boolean removalRequested = parameters.getBoolean("removal_requested", false);
        try
        {
            long docId = parameters.getLong("doc_id");
            DocumentNodeResource node = DocumentNodeResourceImpl.getDocumentNodeResource(
                coralSession, docId);
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            Parameters screenConfig = cmsData.getEmbeddedScreenConfig();
            ProposedDocumentData data = new ProposedDocumentData(screenConfig);
            if(node.isProposedContentDefined())
            {
                data.fromProposal(node, coralSession);
            }
            else
            {
                data.fromNode(node, categoryService, relatedService, htmlService, coralSession);
            }
            if(removalRequested)
            {
                data.setRemovalRequested(true);
                data.toProposal(node);
            }
            else
            {
                node.setProposedContent(null);
            }
            node.update();
            templatingContext.put("result", removalRequested ? "removal_request_submitted" : "removal_request_withdrawn");
        }
        catch(Exception e)
        {
            logger.error("excception", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        Parameters requestParameters = context.getAttribute(RequestParameters.class);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        Subject userSubject = coralSession.getUserSubject();

        long id = requestParameters.getLong("doc_id", -1);
        Resource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, id);
        Permission modifyPermission = coralSession.getSecurity().getUniquePermission(
            "cms.structure.modify");
        Permission modifyOwnPermission = coralSession.getSecurity().getUniquePermission(
            "cms.structure.modify_own");
        if(userSubject.hasPermission(node, modifyPermission))
        {
            return true;
        }
        if(node.getOwner().equals(userSubject)
            && userSubject.hasPermission(node, modifyOwnPermission))
        {
            return true;
        }
        return false;
    }
}
