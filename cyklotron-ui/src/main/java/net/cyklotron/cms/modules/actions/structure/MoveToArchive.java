package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.SubtreeVisitor;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
`*
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: MoveToArchive.java,v 1.2 2005-08-08 09:07:28 rafal Exp $
 */
public class MoveToArchive
    extends BaseStructureAction
{
    private CategoryService categoryService;
    
    private SecurityService securityService;
    
    public MoveToArchive(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        CategoryService categoryService, SecurityService securityService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.categoryService = categoryService;
        this.securityService = securityService;
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            NavigationNodeResource sourceNode = getSourceNode(parameters, coralSession);
            NavigationNodeResource dstNode = getDestinationNode(parameters, coralSession);
            SiteResource srcSite = sourceNode.getSite();
            SiteResource dstSite = dstNode.getSite();
            SubtreeVisitor visitor = new MyVisitor(coralSession, srcSite, dstSite);
            visitor.traverseBreadthFirst(sourceNode);
            structureService.moveToArchive(coralSession, sourceNode, dstNode);
            mvcContext.setView("structure.NaviInfo");
            templatingContext.put("result", "archived_successfully");
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to archive section",e);
        }
        
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            Role srcRole = getSourceNode(parameters, coralSession).getSite().getAdministrator();
            Role dstRole = getDestinationNode(parameters, coralSession).getSite().getAdministrator();
            if(srcRole == null || dstRole == null)
            {
                return false;
            }
            return coralSession.getUserSubject().hasRole(srcRole) && coralSession.getUserSubject().hasRole(dstRole);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check rights",e);
        }
    }
    
    private NavigationNodeResource getSourceNode(Parameters parameters, CoralSession coralSession)
        throws EntityDoesNotExistException
    {
        long nodeId = parameters.getLong("src_node_id");
        return NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
    }
    
    private NavigationNodeResource getDestinationNode(Parameters parameters, CoralSession coralSession)
        throws EntityDoesNotExistException
    {
        long nodeId = parameters.getLong("dst_node_id");
        return NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId);
    }
    
    private class MyVisitor extends SubtreeVisitor
    {
        private CoralSession coralSession;
        
        private SiteResource dstSite;
        
        private SiteResource srcSite;
        
        public MyVisitor(CoralSession coralSession, SiteResource srcSite,
            SiteResource dstSite)
        {
            this.coralSession = coralSession;
            this.dstSite = dstSite;
            this.srcSite = srcSite;
        }
        
        public void visit(NavigationNodeResource node)
        {
            try
            {
                node.getPreferences().remove();
                node.setSite(dstSite);
                node.setStyle(null);
                node.update();
                categoryService.reassignLocalCategories(coralSession, node, srcSite, dstSite);
                securityService.cleanupRoles(coralSession, node, false);
            }
            catch(Exception e)
            {
                throw new RuntimeException("failed to reassign categories", e);
            }
        }
    }
}
