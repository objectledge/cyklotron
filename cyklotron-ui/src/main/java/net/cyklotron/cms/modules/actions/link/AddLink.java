package net.cyklotron.cms.modules.actions.link;

import java.util.Date;
import java.util.List;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.CmsLinkResource;
import net.cyklotron.cms.link.CmsLinkResourceImpl;
import net.cyklotron.cms.link.ExternalLinkResource;
import net.cyklotron.cms.link.ExternalLinkResourceImpl;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.services.workflow.TransitionResource;
import net.cyklotron.services.workflow.WorkflowException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddLink.java,v 1.2 2005-01-24 10:27:01 pablo Exp $
 */
public class AddLink
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
            route(data, "link,AddLink", "invalid_title");
            return;
        }
        if(description.length() > 255)
        {
            route(data, "link,AddLink", "invalid_description");
            return;
        }

        String srcType = parameters.get("src_type","external");
        int lsid = parameters.getInt("lsid", -1);
        if(lsid == -1)
        {
            throw new ProcessingException("Links root id not found");
        }

        long startTime = parameters.getLong("start_time", 0);
        String eternal = parameters.get("end_time","");

        long endTime = parameters.getLong("end_time", 0);
        // TODO altText handling
        // String altText = parameters.get("alt_text","");

        Date start = new Date(startTime);
        Date end = new Date(endTime);

        try
        {
            LinkRootResource linksRoot = LinkRootResourceImpl.getLinkRootResource(coralSession, lsid);
            BaseLinkResource linkResource = null;

            if(srcType.equals("cms"))
            {
                String intTarget = parameters.get("int_target","");
                String structurePath = parameters.get("structure_path","");
                Resource[] section = coralSession.getStore().getResourceByPath(structurePath + intTarget);
                if(section.length != 1 || !(section[0] instanceof NavigationNodeResource))
                {
                    route(data, "link,AddLink", "invalid_target");
                    return;
                }
                linkResource = CmsLinkResourceImpl.
                    createCmsLinkResource(coralSession, title, linksRoot, subject);
                ((CmsLinkResource)linkResource).setNode((NavigationNodeResource)section[0]);
            }
            else
            {
                linkResource = ExternalLinkResourceImpl.
                    createExternalLinkResource(coralSession, title, linksRoot, subject);
                String target = parameters.get("ext_target","");
                if(!(target.startsWith("http://") ||  target.startsWith("https://")))
                {
                    target = "http://"+target;
                }
                ((ExternalLinkResource)linkResource).setTarget(target);
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
            Resource workflowRoot = linksRoot.getParent().getParent().getParent().getParent();
            workflowService.assignState(workflowRoot, linkResource, subject);
            String transitionName = parameters.get("transition","");
            if(transitionName.length() != 0)
            {
                TransitionResource[] transitions = workflowService.getTransitions(linkResource.getState());
                for(int i = 0; i<transitions.length; i++)
                {
                    if(transitions[i].getName().equals(transitionName))
                    {
                        linkResource.setState(transitions[i].getTo());
                        workflowService.enterState(linkResource, transitions[i].getTo());
                        break;
                    }
                }
            }
            linkResource.update(subject);
            long pid = parameters.getLong("pid", -1);
            if(pid != -1)
            {
                PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
                List links = poolResource.getLinks();
                links.add(linkResource);
                poolResource.setLinks(links);
                poolResource.update(subject);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        catch(ValueRequiredException e)
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
        templatingContext.put("result","added_successfully");
    }
}


