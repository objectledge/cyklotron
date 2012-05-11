package net.cyklotron.cms.modules.actions.documents;

import java.util.Collection;
import java.util.Iterator;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;
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
import net.cyklotron.cms.documents.internal.DocumentRenderingHelper;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class FlushRenderingCache
    extends BaseDocumentAction
{

    private final CacheFactory cacheFactory;

    public FlushRenderingCache(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService, CacheFactory cacheFactory)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
        this.cacheFactory = cacheFactory;
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite(context);

        Collection drhCache = cacheFactory.getInstance("docRenderingHelpers").values();
        synchronized(drhCache)
        {
            for(Iterator i = drhCache.iterator(); i.hasNext();)
            {
                DocumentRenderingHelper drh = (DocumentRenderingHelper)i.next();

                if(drh.getDocument().getSite().equals(site))
                {
                    i.remove();
                }
            }
        }
    }
}
