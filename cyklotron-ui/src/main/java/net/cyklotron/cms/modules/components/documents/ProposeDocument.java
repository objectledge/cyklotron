package net.cyklotron.cms.modules.components.documents;

import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.AmbigousNameException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * ProposeDocument component displays link to propose document screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ProposeDocument.java,v 1.1 2005-01-24 04:35:16 pablo Exp $
 */
public class ProposeDocument
    extends SkinableCMSComponent
{
    StructureService structureService;
    
    public ProposeDocument()
    {
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            NavigationNodeResource node = getHomePage(context);
            Parameters pc = node.getPreferences();
            String path = pc.get("propose_document.path","/propose_document");
            path = node.getPath()+path;
            Resource proposeDocumentNode = coralSession.getStore().getUniqueResourceByPath(path);
            templatingContext.put("propose_document_node",proposeDocumentNode);
        }
        catch(Exception e)
        {
            componentError(context, "Unknown Error "+e.getMessage());
        }
    }
}
