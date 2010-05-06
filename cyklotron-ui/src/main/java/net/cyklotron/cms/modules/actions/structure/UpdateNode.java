package net.cyklotron.cms.modules.actions.structure;

import java.util.Date;

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
import net.cyklotron.cms.documents.DocumentNodeResource;
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
 * @version $Id: UpdateNode.java,v 1.13 2008-03-15 13:28:11 pablo Exp $
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
    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // basic setup
        
        Subject subject = coralSession.getUserSubject(); 
        NavigationNodeResource node;

        long nodeId = parameters.getLong("node_id", -1);
        if(nodeId == -1)
        {
            node = getNode(context);
        }
        else
        {
            try
            {
                node = (NavigationNodeResource)coralSession.getStore().getResource(nodeId);
            }
            catch(EntityDoesNotExistException e)
            {
                templatingContext.put("result", "exception");
                logger.error("Node does not exist exception: ", e);
                templatingContext.put("trace", new StackTrace(e));
                return;
            }
        }
        
        // parameters check
        if(!checkParameters(parameters, mvcContext, templatingContext))
        {
            return;
        }

        // parameters setup
        String name = parameters.get("name","");
        String title = parameters.get("title","");
        String description = parameters.get("description","");
        String redactorsNote = parameters.get("redactors_note", "");
        int priority = parameters.getInt("priority", structureService.getDefaultPriority());
        priority = structureService.getAllowedPriority(coralSession, node, subject, priority);
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
            boolean updateTimeStamp = false;
            if(!name.equals(node.getName()))
            {
                coralSession.getStore().setName(node, name);
            }

            if(!title.equals(node.getTitle()))
            {
                updateTimeStamp = true;
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

            if(node instanceof DocumentNodeResource
                && !redactorsNote.equals(((DocumentNodeResource)node).getRedactorsNote()))
            {
                ((DocumentNodeResource)node).setRedactorsNote(redactorsNote);
            }

            Date oldStartDate = node.getValidityStart();            
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
            if(structureService.updateNode(coralSession, node, name, updateTimeStamp, subject))
            {
                templatingContext.put("result","updated_successfully_and_taken");
            }
            else
            {                
                templatingContext.put("result","updated_successfully");
            }
            boolean forceTimeStructure = parameters.getBoolean("forceTimeStructure",false);
            boolean isStartDate = parameters.get("validity_start").length() > 0;
            //boolean isInTimeStructure = isInTimeStructure(node);
            String timeStructureType = structureService.getTimeStructureType(node);
            if(isStartDate && (forceTimeStructure || 
                            !timeStructureType.equals(StructureService.NONE_CALENDAR_TREE_STRUCTURE)))
            {
                //Resource newParent = null;
                /*
                if(isInTimeStructure)
                {
                    newParent = parent.getParent().getParent().getParent();
                }
                else
                {
                    newParent = parent;
                }
                */
                Resource newParent = structureService.getCalendarTreeRoot(node, timeStructureType);
                if(forceTimeStructure)
                {
                    newParent = structureService.getParent(coralSession, newParent, 
                        node.getValidityStart(), StructureService.DAILY_CALENDAR_TREE_STRUCTURE, subject);
                }
                else
                {
                    newParent = structureService.getParent(coralSession, newParent, 
                        node.getValidityStart(), timeStructureType, subject);
                }
                if(!newParent.equals(parent))
                {
                    coralSession.getStore().setParent(node, newParent);
                }
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

    private boolean isInTimeStructure(NavigationNodeResource node)
    {
        try
        {
            long day = Long.parseLong(node.getParent().getName());
            long month = Long.parseLong(node.getParent().getParent().getName());
            long year = Long.parseLong(node.getParent().getParent().getParent().getName());
            if(day < 1 || day > 31)
            {
                return false;
            }
            if(month < 1 || month > 12)
            {
                return false;
            }
            if(year < 1970)
            {
                return false;
            }
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    @Override
    protected String getViewName()
    {
        return "structure.EditNode";
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return getCmsData(context).getNode().canModify(coralSession, coralSession.getUserSubject());
    }
}
