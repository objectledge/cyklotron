package net.cyklotron.cms.modules.actions.link;

import java.util.Date;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.ExternalLinkResource;
import net.cyklotron.cms.link.ExternalLinkResourceImpl;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ProposeLink.java,v 1.1 2005-01-24 04:34:56 pablo Exp $
 */
public class ProposeLink
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
                    createExternalLinkResource(coralSession, title, linksRoot, subject);
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
            workflowService.assignState(workflowRoot, linkResource, subject);
            linkResource.update(subject);
        }
        catch(Exception e)
        {
            templatingContext.put("propose_link_result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        templatingContext.put("propose_link_result","added_successfully");
    }
    
	public boolean checkAccess(RunData data)
	{
		return true;	
	}
}


