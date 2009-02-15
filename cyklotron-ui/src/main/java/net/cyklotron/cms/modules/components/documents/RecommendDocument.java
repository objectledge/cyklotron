package net.cyklotron.cms.modules.components.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * RecomendDocument component.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RecommendDocument.java,v 1.2 2005-01-25 11:24:19 pablo Exp $
 */
public class RecommendDocument
    extends SkinableCMSComponent
{
    protected StructureService structureService;    
    
    public RecommendDocument(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        StructureService structureService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.structureService = structureService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            NavigationNodeResource node = getHomePage(context);
            //Parameters pc = node.getPreferences();
            Parameters pc = getConfiguration();
            //String path = pc.get("recommend_document.path","/recommend_document");
			String path = pc.get("recommend_document_path");
            //System.out.println("")
            
            path = node.getPath()+path;
			Resource recommendDocumentNode = coralSession.getStore().getUniqueResourceByPath(path);
			templatingContext.put("recommend_document_node",recommendDocumentNode);
        }
        catch(Exception e)
        {
            componentError(context, "Unknown Error "+e.getMessage());
        }
    }
}
