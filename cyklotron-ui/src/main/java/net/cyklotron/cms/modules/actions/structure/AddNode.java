package net.cyklotron.cms.modules.actions.structure;

import java.util.Date;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeAlreadyExistException;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;

/**
 * Create new navigation node in document tree.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AddNode.java,v 1.2 2005-01-24 10:26:59 pablo Exp $
 */
public class AddNode
    extends BaseAddEditNodeAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // basic setup
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        // parameters check
        if(!checkParameters(data))
        {
            return;
        }

        // parameters setup
		boolean calendarTree = parameters.getBoolean("calendar_tree", false);
        String name = parameters.get("name","");
        String title = parameters.get("title","");
        String description = parameters.get("description","");
		long thumbnailId = parameters.getLong("thumbnail_id", -1);
        // action setup
        long styleId = parameters.getLong("style_id", -1);
        StyleResource style = null;
        if (styleId != -1)
        {
            try
            {
                style = StyleResourceImpl.getStyleResource(coralSession, styleId);
            }
            catch(EntityDoesNotExistException e)
            {
                templatingContext.put("result","exception");
                log.error("ARL exception: ", e);
                data.getContext().put("trace", new StackTrace(e));
                return;
            }
        }
		NavigationNodeResource node = null;
        NavigationNodeResource parent = getNode(context);
		try
		{
			if(calendarTree && parameters.get("validity_start").length() > 0)
			{
				parent = structureService.getParent(parent, new Date(parameters.get("validity_start").asLong()), subject);
			}

	        // get greatest sequence number
    	    int sequence = 0;
        	Resource[] children = coralSession.getStore().getResource(parent);
        	for(int i=0; i<children.length; i++)
	        {
    	        Resource child = children[i];
        	    if(child instanceof NavigationNodeResource)
            	{
                	int childSeq = ((NavigationNodeResource)child).getSequence(0);
                	sequence = sequence<childSeq ? childSeq : sequence;
            	}
        	}

        	// action execution
	        node = structureService.addDocumentNode(name, title, parent, subject);
            node.setDescription(description);
            node.setSequence(sequence);
            setValidity(data, node);
            node.setStyle(style);
            node.setPriority(0);
			if(thumbnailId != -1)
			{
				FileResource thumbnail = FileResourceImpl.getFileResource(coralSession, thumbnailId);
				node.setThumbnail(thumbnail);				
			}
            structureService.updateNode(node, name, subject);
            if(structureService.isWorkflowEnabled())
            {
                Permission permission = coralSession.getSecurity().getUniquePermission("cms.structure.modify_own");
                if(subject.hasPermission(node,permission))
                {
                    structureService.enterState(node, "taken", subject);
                }
                else
                {
                    structureService.enterState(node, "new", subject);
                }
            }
        }
        catch(NavigationNodeAlreadyExistException e)
        {
            errorResult(data, "navi_name_repeated");
            return;
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }

        // make the newly created node a current node
        parameters.set("node_id", node.getIdString());
        // remove cms data to setu new node
        CmsData.removeCmsData(data);
        
        templatingContext.put("result","added_successeditfully");
    }

    protected String getViewName()
    {
        return "structure,AddNode";
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return getCmsData(context).getNode(context).canAddChild(coralSession.getUserSubject());
    }
}
