package net.cyklotron.cms.modules.components.documents;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * RecomendDocument component.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RecommendDocument.java,v 1.1 2005-01-24 04:35:16 pablo Exp $
 */
public class RecommendDocument
    extends SkinableCMSComponent
{
    StructureService structureService;
    
    public RecommendDocument()
    {
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
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
