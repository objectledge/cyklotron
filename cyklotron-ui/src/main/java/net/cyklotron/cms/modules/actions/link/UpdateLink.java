package net.cyklotron.cms.modules.actions.link;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.CmsLinkResource;
import net.cyklotron.cms.link.ExternalLinkResource;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateLink.java,v 1.9 2005-06-13 11:08:28 rafal Exp $
 */
public class UpdateLink
    extends BaseLinkAction
{

    private final CoralSessionFactory coralSessionFactory;
    
    public UpdateLink(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, LinkService linkService, WorkflowService workflowService,
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, linkService, workflowService);
        
        this.coralSessionFactory = coralSessionFactory;
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        String title = parameters.get("title","");
        String description = parameters.get("description","");

        if(title.length() < 1 || title.length() > 255)
        {
            route(mvcContext, templatingContext, "link.EditLink", "invalid_title");
            return;
        }

        if(description.length() > 255)
        {
            route(mvcContext, templatingContext, "link.EditLink", "invalid_description");
            return;
        }

        int lid = parameters.getInt("lid", -1);
        if(lid == -1)
        {
            throw new ProcessingException("Link id not found");
        }

        // time stuff
        long startTime = parameters.getLong("start_time", 0);
        long endTime = parameters.getLong("end_time", 0);
        String eternal = parameters.get("end_time","");
        Date start = new Date(startTime);
        Date end = new Date(endTime);
        
        try
        {
            BaseLinkResource linkResource = BaseLinkResourceImpl.getBaseLinkResource(coralSession, lid);
            if(!linkResource.getName().equals(title)
                && !coralSession.getStore().isValidResourceName(title))
            {
                route(mvcContext, templatingContext, "link.EditLink", "invalid_name");
                return;
            }

            if(linkResource instanceof CmsLinkResource)
            {
                String intTarget = parameters.get("int_target","");
                String structurePath = parameters.get("structure_path","");
                Resource[] section = coralSession.getStore().getResourceByPath(structurePath + intTarget);
                if(section.length != 1 || !(section[0] instanceof NavigationNodeResource))
                {
                    route(mvcContext, templatingContext, "link.EditLink", "invalid_target");
                    return;
                }
                ((CmsLinkResource)linkResource).setNode((NavigationNodeResource)section[0]);
            }
            else
            {
                String target = parameters.get("target","");
                if(!(target.startsWith("http://") ||  target.startsWith("https://")))
                {
                    target = "http://"+target;
                }
                ((ExternalLinkResource)linkResource).setTarget(target);
            }
            if(!linkResource.getName().equals(title))
            {
                try
                {
                    coralSession.getStore().setName(linkResource, title);
                }
                catch(InvalidResourceNameException e)
                {
                    // we've just checked it, right?
                    throw new ProcessingException("unexpected exception", e);
                }
            }
            linkResource.setDescription(description);
            linkResource.setStartDate(start);
            linkResource.setEndDate(end);
            if(eternal.equals(""))
            {
                linkResource.setEternal(true);
            }
            else
            {
                linkResource.setEternal(false);
            }
            if(linkResource.getState().getName().equals("active") ||
               linkResource.getState().getName().equals("expired"))
            {
                StateResource[] states = workflowService.getStates(coralSession, workflowService.getAutomaton(coralSession, linkResource.getState()),false);
                int i = 0;
                for(;i < states.length; i++)
                {
                    if(states[i].getName().equals("ready"))
                    {
                        linkResource.setState(states[i]);
                        workflowService.enterState(coralSession, linkResource,states[i]);
                        break;
                    }
                }
                if(i == states.length)
                {
                    templatingContext.put("result","state_not_found");
                    return;
                }
            }
            linkResource.update();
            
            // here update pools that link belongs to
            long[] params = parameters.getLongs("pool_id");
            Set<Long> selectionSet = new HashSet<Long>();
            for(int i = 0; i < params.length; i++)
            {
            	selectionSet.add(new Long(params[i]));
            }
			Resource[] resources = coralSession.getStore().getResource(linkResource.getParent());
            
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    List links = ((PoolResource)resources[i]).getLinks();
                    ResourceList newLinks = new ResourceList(coralSessionFactory);
                    boolean update = false;
                    if(selectionSet.contains(resources[i].getIdObject()))
                    {
                        // we are going to add if not exists
                        if(links == null)
                        {
                            newLinks.add(linkResource);
                            update = true;
                        }
                        else
                        {
                            boolean found = false;
                            for(int j = 0; j < links.size(); j++)
                            {
                                Resource link = (Resource)links.get(j);
                                if(linkResource.equals(link))
                                {
                                    found = true;
                                }
                                newLinks.add(link);
                            }
                            if(!found)
                            {
                                newLinks.add(linkResource);
                            }
                            if(newLinks.size() > links.size())
                            {
                                update = true;
                            }
                        }
                    }
                    else
                    {
                        // we are going to del if exists
                        if(links != null)
                        {
                            for(int j = 0; j < links.size(); j++)
                            {
                                Resource link = (Resource)links.get(j);
                                if(!linkResource.equals(link))
                                {
                                    newLinks.add(link);
                                }
                            }
                            if(newLinks.size() < links.size())
                            {
                                update = true;
                            }
                        }
                    }
                    if(update)
                    {
                        ((PoolResource)resources[i]).setLinks(newLinks);
                        resources[i].update();
                    }
                }
            }
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}


