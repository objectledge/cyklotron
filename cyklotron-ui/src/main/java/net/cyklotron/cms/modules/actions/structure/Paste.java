package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.schema.CircularDependencyException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Paste action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Paste.java,v 1.3 2005-01-25 08:24:46 pablo Exp $
 */
public class Paste extends BaseCopyPasteAction
{
    public Paste(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        
        long id = parameters.getLong("node_id", -1);
        if (id == -1)
        {
            templatingContext.put("result", "parameter_not_found");
            return;
        }
        try
        {
            String mode = (String)httpContext.getSessionAttribute(CLIPBOARD_MODE);
            NavigationNodeResource node = getClipboardNode(httpContext, coralSession);
            if (node == null || mode == null)
            {
                templatingContext.put("result", "clipboard_empty");
                return;
            }
            Resource parent = coralSession.getStore().getResource(id);
            if(parent.equals(node))
            {
				templatingContext.put("result", "invalid_destination");
				return;
            }
            Subject subject = coralSession.getUserSubject();
			SiteResource site1 = ((NavigationNodeResource)parent).getSite();
			SiteResource site2 = ((NavigationNodeResource)node).getSite();
			if(!site1.equals(site2))
			{
				templatingContext.put("result", "cross_site_copy_forbidden");
				return;
			}
			
            if (mode.equals("copy"))
            {
            	Resource[] resources = coralSession.getStore().getResource(parent, node.getName());
            	if(resources.length > 0)
            	{
            		templatingContext.put("result", "already_exists");
            		return;
            	}
                try
                {
                	coralSession.getStore().copyTree(node, parent, node.getName());
                }
                catch (CircularDependencyException e)
                {
                    templatingContext.put("result", "cannot_copy_to_descendant");
                    return;
                }
            }
            else
            {
                try
                {
                    coralSession.getStore().setParent(node, parent);
                }
                catch (CircularDependencyException e)
                {
                    templatingContext.put("result", "cannot_move_to_descendant");
                    return;
                }
            }
        }
        catch (Exception e)
        {
            templatingContext.put("result", "exception");
            logger.error("StructureException: ", e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        templatingContext.put("result", "moved_successfully");
    }

    public NavigationNodeResource getClipboardNode(HttpContext httpContext, CoralSession coralSession) throws Exception
    {
        Long nodeId = (Long)httpContext.getSessionAttribute(CLIPBOARD_KEY);
        if (nodeId == null)
        {
            return null;
        }
        return NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, nodeId.longValue());
    }

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        try
        {
            Subject subject = coralSession.getUserSubject();
            return canMove(context, coralSession.getUserSubject(), getClipboardNode(httpContext, coralSession));
        }
        catch (Exception e)
        {
            logger.error("Exception occured during access checking ", e);
            return false;
        }
    }
}
