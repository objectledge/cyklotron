package net.cyklotron.cms.modules.actions.link;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
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
import net.cyklotron.cms.link.ExternalLinkResource;
import net.cyklotron.cms.link.ExternalLinkResourceImpl;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ProposeLink.java,v 1.3 2005-01-25 07:15:09 pablo Exp $
 */
public class ProposeLink
    extends BaseLinkAction
{
    public ProposeLink(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, LinkService linkService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, linkService, workflowService);
        // TODO Auto-generated constructor stub
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

        if(title.length() < 1 || title.length() > 64)
        {
			templatingContext.put("propose_link_result","invalid_title");
            return;
        }
        if(description.length() > 256)
        {
			templatingContext.put("propose_link_result","invalid_description");
            return;
        }

        int lsid = parameters.getInt("lsid", -1);
        if(lsid == -1)
        {
            throw new ProcessingException("Links root id not found");
        }

        long startTime = parameters.getLong("start_time", 0);
        String eternal = parameters.get("end_time","");
        long endTime = parameters.getLong("end_time", 0);
        Date start = new Date(startTime);
        Date end = new Date(endTime);

        try
        {
            LinkRootResource linksRoot = LinkRootResourceImpl.getLinkRootResource(coralSession, lsid);
            ExternalLinkResource linkResource = ExternalLinkResourceImpl.
                    createExternalLinkResource(coralSession, title, linksRoot);
            String target = parameters.get("ext_target","");
            if(!(target.startsWith("http://") ||  target.startsWith("https://")))
            {
                target = "http://"+target;
            }
            linkResource.setTarget(target);
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
            linkResource.update();
        }
        catch(Exception e)
        {
            templatingContext.put("propose_link_result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        templatingContext.put("propose_link_result","added_successfully");
    }
    
	public boolean checkAccessRights(Context context)
	{
		return true;	
	}
}


