package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.forms.FormsService;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;


import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.documents.keywords.KeywordResource;
import net.cyklotron.cms.documents.keywords.KeywordResourceImpl;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 */
public class EditKeyword
    extends BaseDocumentScreen
{
    public EditKeyword(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService,
        DocumentService documentService, IntegrationService integrationService,
        FormsService formsService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService,
                        documentService, integrationService);
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long keywordId = parameters.getLong("keywordId");
            KeywordResource keyword = KeywordResourceImpl.getKeywordResource(coralSession, keywordId);
            templatingContext.put("keyword", keyword);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to lookup resource", e);
        }
    }
    
    public boolean checkAccessRights(Context context)
                    throws ProcessingException
    {
          CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
          return coralSession.getUserSubject().hasRole(getSite().getAdministrator());
    }
}
