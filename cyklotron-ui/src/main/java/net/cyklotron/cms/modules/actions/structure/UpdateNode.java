package net.cyklotron.cms.modules.actions.structure;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateNode.java,v 1.1 2005-01-24 04:33:55 pablo Exp $
 */
public class UpdateNode
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
        String name = parameters.get("name","");
        String title = parameters.get("title","");
        String description = parameters.get("description","");
		int priority = parameters.getInt("priority", 0);
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
                data.getContext().put("trace", StringUtils.stackTrace(e));
                return;
            }
        }

        NavigationNodeResource node = getNode(context);
        Resource parent = node.getParent();

        // check name
        if(!node.getName().equals(name))
        {
            Resource[] resources = coralSession.getStore().getResource(parent, name);
            if(resources.length > 0)
            {
                errorResult(data, "navi_name_repeated");
                return;
            }
        }

        // action execution
        try
        {
            if(!name.equals(node.getName()))
            {
                coralSession.getStore().setName(node, name);
            }

            if(!title.equals(node.getTitle()))
            {
                try
                {
                    node.setTitle(title);
                }
                catch(ValueRequiredException e)
                {
                    // Should never happen
                    // title is already checked for empty value
                }
            }

            if(!description.equals(node.getDescription()))
            {
                node.setDescription(description);
            }

            setValidity(data, node);

            if((node.getStyle() != null && !node.getStyle().equals(style))
               || (node.getStyle() == null && style != null))
            {
                node.setStyle(style);
            }
			node.setPriority(priority);
			if(thumbnailId != -1)
			{
				FileResource thumbnail = FileResourceImpl.getFileResource(coralSession, thumbnailId);
                node.setThumbnail(thumbnail);
			}
            else
            {
                node.setThumbnail(null);
            }
            structureService.updateNode(node, name, subject);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("StructureException: ",e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    protected String getViewName()
    {
        return "structure,EditNode";
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return getCmsData(context).getNode(context).canModify(coralSession.getUserSubject());
    }
}
