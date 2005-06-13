package net.cyklotron.cms.modules.actions.link;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
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
import net.cyklotron.cms.link.CmsLinkResource;
import net.cyklotron.cms.link.CmsLinkResourceImpl;
import net.cyklotron.cms.link.ExternalLinkResource;
import net.cyklotron.cms.link.ExternalLinkResourceImpl;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.TransitionResource;
import net.cyklotron.cms.workflow.WorkflowException;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddLink.java,v 1.6 2005-06-13 11:08:28 rafal Exp $
 */
public class AddLink
    extends BaseLinkAction
{
    

    public AddLink(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        LinkService linkService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, linkService, workflowService);
        
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
            route(mvcContext, templatingContext, "link.AddLink", "invalid_title");
            return;
        }
        if(description.length() > 255)
        {
            route(mvcContext, templatingContext, "link.AddLink", "invalid_description");
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
                    route(mvcContext, templatingContext, "link.AddLink", "invalid_target");
                    return;
                }
                try
                {
                    linkResource = CmsLinkResourceImpl.
                        createCmsLinkResource(coralSession, title, linksRoot);
                }
                catch(InvalidResourceNameException e)
                {
                    route(mvcContext, templatingContext, "link.AddLink", "invalid_name");
                    return;
                }
                ((CmsLinkResource)linkResource).setNode((NavigationNodeResource)section[0]);
            }
            else
            {
                try
                {
                    linkResource = ExternalLinkResourceImpl.
                        createExternalLinkResource(coralSession, title, linksRoot);
                }
                catch(InvalidResourceNameException e)
                {
                    route(mvcContext, templatingContext, "link.AddLink", "invalid_name");
                    return;
                }
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
            workflowService.assignState(coralSession, workflowRoot, linkResource);
            String transitionName = parameters.get("transition","");
            if(transitionName.length() != 0)
            {
                TransitionResource[] transitions = workflowService.getTransitions(coralSession, linkResource.getState());
                for(int i = 0; i<transitions.length; i++)
                {
                    if(transitions[i].getName().equals(transitionName))
                    {
                        linkResource.setState(transitions[i].getTo());
                        workflowService.enterState(coralSession, linkResource, transitions[i].getTo());
                        break;
                    }
                }
            }
            linkResource.update();
            long pid = parameters.getLong("pid", -1);
            if(pid != -1)
            {
                PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
                ResourceList links = poolResource.getLinks();
                links.add(linkResource);
                poolResource.setLinks(links);
                poolResource.update();
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
        templatingContext.put("result","added_successfully");
    }
}


