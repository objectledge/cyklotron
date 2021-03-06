package net.cyklotron.cms.modules.actions.structure;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentAliasResource;
import net.cyklotron.cms.documents.DocumentAliasResourceImpl;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeAlreadyExistException;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 * Create new navigation node in document tree.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AddNode.java,v 1.11 2008-03-15 13:28:12 pablo Exp $
 */
public class AddAlias
    extends BaseAddEditNodeAction
{
    public AddAlias(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        Long parentNodeId = parameters.getLong("node_id",-1L);
        Long originalNodeId = parameters.getLong("original_node_id",-1L);
        String name = parameters.get("name","");
        String title = parameters.get("title","");
        
        if(parentNodeId == -1L || originalNodeId == -1L)
        {
            return;
        }
       
		try
		{
	        NavigationNodeResource parent = (NavigationNodeResource)coralSession.getStore().getResource(parentNodeId);
            DocumentNodeResource originalDocument = (DocumentNodeResource)coralSession.getStore().getResource(originalNodeId);
            DocumentAliasResource node = structureService.addDocumentAlias(coralSession,
                originalDocument, name, title, parent, subject);
            parameters.set("node_id", node.getIdString());
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            logger.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        cmsDataFactory.removeCmsData(context);
        templatingContext.put("result","added_successeditfully");
    }

    protected String getViewName()
    {
        return "structure.AddAlias";
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        RequestParameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            NavigationNodeResource node = getCmsData(context).getNode();
            NavigationNodeResource originalNode = NavigationNodeResourceImpl.getNavigationNodeResource(
                coralSession, parameters.getLong("original_node_id"));
            Permission addInboundAlias = coralSession.getSecurity().getUniquePermission(
                "cms.structure.add_inbound_alias");
            return node.canAddChild(coralSession, coralSession.getUserSubject())
                && coralSession.getUserSubject().hasPermission(originalNode, addInboundAlias);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("missing or invalid parameters", e);
        }
    }
}
