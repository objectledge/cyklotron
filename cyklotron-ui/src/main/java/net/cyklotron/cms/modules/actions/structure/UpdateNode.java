package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateNode.java,v 1.6 2005-06-03 08:24:43 rafal Exp $
 */
public class UpdateNode
    extends BaseAddEditNodeAction
{
    public UpdateNode(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // basic setup
        
        Subject subject = coralSession.getUserSubject();

        // parameters check
        if(!checkParameters(parameters, mvcContext, templatingContext))
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
                logger.error("ARL exception: ", e);
                templatingContext.put("trace", new StackTrace(e));
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
                route(mvcContext, templatingContext, getViewName(), "navi_name_repeated");
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

            setValidity(parameters, node);

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
            if(structureService.updateNode(coralSession, node, name, subject))
            {
                templatingContext.put("result","updated_successfully_and_taken");
            }
            else
            {                
                templatingContext.put("result","updated_successfully");
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            logger.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
    }

    protected String getViewName()
    {
        return "structure.EditNode";
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return getCmsData(context).getNode().canModify(context, coralSession.getUserSubject());
    }
}
