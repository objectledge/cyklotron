package net.cyklotron.cms.modules.actions.structure;

import net.labeo.services.resource.CircularDependencyException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 * Paste action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Paste.java,v 1.2 2005-01-24 10:26:59 pablo Exp $
 */
public class Paste extends BaseCopyPasteAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        Context context = data.getContext();
        long id = parameters.getLong("node_id", -1);
        if (id == -1)
        {
            templatingContext.put("result", "parameter_not_found");
            return;
        }
        try
        {
            String mode = (String)httpContext.getSessionAttribute(CLIPBOARD_MODE);
            NavigationNodeResource node = getClipboardNode(data);
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
                	coralSession.getStore().copyTree(node, parent, node.getName(), subject);
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
            log.error("StructureException: ", e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        templatingContext.put("result", "moved_successfully");
    }

    public NavigationNodeResource getClipboardNode(RunData data) throws Exception
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
        try
        {
            Subject subject = coralSession.getUserSubject();
            return canMove(coralSession.getUserSubject(), getClipboardNode(data));
        }
        catch (Exception e)
        {
            log.error("Exception occured during access checking ", e);
            return false;
        }
    }
}
