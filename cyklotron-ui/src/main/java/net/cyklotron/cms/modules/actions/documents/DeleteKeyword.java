package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.forms.FormsService;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.documents.keywords.KeywordResource;
import net.cyklotron.cms.documents.keywords.KeywordResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class DeleteKeyword
    extends BaseDocumentAction
{
    public DeleteKeyword(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long keywordId = parameters.getLong("keywordId");
            KeywordResource keyword = KeywordResourceImpl.getKeywordResource(coralSession,
                keywordId);
            coralSession.getStore().deleteResource(keyword);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to remove keyword", e);
        }
    }
    
    public boolean checkAccessRights(Context context)
                    throws ProcessingException
    {
          CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
          return coralSession.getUserSubject().hasRole(getSite(context).getAdministrator());
    }
}
