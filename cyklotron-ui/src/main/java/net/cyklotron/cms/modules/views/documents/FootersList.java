package net.cyklotron.cms.modules.views.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
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
import net.cyklotron.cms.documents.FooterResource;
import net.cyklotron.cms.documents.table.FooterSequenceComparator;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 */
public class FootersList
    extends BaseDocumentScreen
{
    public static final String FOOTER_SEQUENCE = "cms_documents_footer_sequence";
    
    public FootersList(Context context, Logger logger,
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
            boolean reset = parameters.getBoolean("reset", false);
            SiteResource site = getSite();
            Resource root = documentService.getFootersRoot(coralSession, site);
            Resource[] resources = coralSession.getStore().getResource(root);
            List footers = new ArrayList();
            HashMap footersMap = new HashMap();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof FooterResource)
                {
                    footers.add(resources[i]);
                    footersMap.put(resources[i].getIdObject(), resources[i]);
                }
            }
            List footersIds = (List)httpContext.getSessionAttribute(FOOTER_SEQUENCE);
            if(footersIds == null || reset)
            {
                Comparator comparator = new FooterSequenceComparator();
                Collections.sort(footers,comparator);
                footersIds = new ArrayList();
                for(int i = 0; i < footers.size(); i++)
                {
                    footersIds.add(((Resource)footers.get(i)).getIdObject());
                }
                httpContext.setSessionAttribute(FOOTER_SEQUENCE,footersIds);
            }
            templatingContext.put("childrenIds",footersIds);
            templatingContext.put("childrenMap",footersMap);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to lookup resource", e);
        }
    }
}
