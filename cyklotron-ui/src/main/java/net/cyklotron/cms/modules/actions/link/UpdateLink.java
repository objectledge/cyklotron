package net.cyklotron.cms.modules.actions.link;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.CmsLinkResource;
import net.cyklotron.cms.link.ExternalLinkResource;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.services.workflow.StateResource;
import net.cyklotron.services.workflow.WorkflowException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateLink.java,v 1.2 2005-01-24 10:27:01 pablo Exp $
 */
public class UpdateLink
    extends BaseLinkAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        String title = parameters.get("title","");
        String description = parameters.get("description","");

        if(title.length() < 1 || title.length() > 255)
        {
            route(data, "link,EditLink", "invalid_title");
            return;
        }

        if(description.length() > 255)
        {
            route(data, "link,EditLink", "invalid_description");
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
            if(linkResource instanceof CmsLinkResource)
            {
                String intTarget = parameters.get("int_target","");
                String structurePath = parameters.get("structure_path","");
                Resource[] section = coralSession.getStore().getResourceByPath(structurePath + intTarget);
                if(section.length != 1 || !(section[0] instanceof NavigationNodeResource))
                {
                    route(data, "link,EditLink", "invalid_target");
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
                coralSession.getStore().setName(linkResource, title);
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
                StateResource[] states = workflowService.getStates(workflowService.getAutomaton(linkResource.getState()),false);
                int i = 0;
                for(;i < states.length; i++)
                {
                    if(states[i].getName().equals("ready"))
                    {
                        linkResource.setState(states[i]);
                        workflowService.enterState(linkResource,states[i]);
                        break;
                    }
                }
                if(i == states.length)
                {
                    templatingContext.put("result","state_not_found");
                    return;
                }
            }
            linkResource.update(subject);
            
            // here update pools that link belongs to
            Parameter[] parameters = parameters.getArray("pool_id");
            Set selectionSet = new HashSet();
            for(int i = 0; i < parameters.length; i++)
            {
            	selectionSet.add(new Long(parameters[i].asLong(-1)));
            }
			Resource[] resources = coralSession.getStore().getResource(linkResource.getParent());
			for(int i = 0; i < resources.length; i++)
			{
				if(resources[i] instanceof PoolResource)
				{
					List links = ((PoolResource)resources[i]).getLinks();
					List newLinks = new ArrayList();
					boolean found = false;
					if(links != null)
					{
					    for(int j = 0; j < links.size(); j++)
					    {
					        Resource link = (Resource)links.get(j);
					        if(linkResource.equals(link))
					        {
					            found = true;
					            if(selectionSet.contains(resources[i].getIdObject()))
					            {
					                newLinks.add(link);
					            }	
					        }
					        else
					        {
					            newLinks.add(link);
					        }
					    }
					}
					if(!found && selectionSet.contains(resources[i].getIdObject()))
					{
						newLinks.add(linkResource);
					}
					((PoolResource)resources[i]).setLinks(newLinks);
					resources[i].update(subject);
				}
			}
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        catch(WorkflowException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}


