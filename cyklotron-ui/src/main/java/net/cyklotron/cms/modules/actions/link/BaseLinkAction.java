package net.cyklotron.cms.modules.actions.link;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.link.LinkConstants;
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.WorkflowService;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseLinkAction.java,v 1.1 2005-01-24 04:34:57 pablo Exp $
 */
public abstract class BaseLinkAction
    extends BaseCMSAction
    implements LinkConstants
{
    /** logging facility */
    protected Logger log;

    /** link service */
    protected LinkService linkService;

    /** workflow service */
    protected WorkflowService workflowService;

    public BaseLinkAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(LinkService.LOGGING_FACILITY);
        linkService = (LinkService)broker.getService(LinkService.SERVICE_NAME);
        workflowService = (WorkflowService)broker.getService(WorkflowService.SERVICE_NAME);
    }

    public LinkRootResource getLinkRoot(RunData data)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData(context);
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site selected");
        }
        try
        {
            return linkService.getLinkRoot(site);
        }
        catch(LinkException e)
        {
            throw new ProcessingException("failed to lookup links root");
        }
    }

    public boolean checkAccess(RunData data)
    {
        try
        {
            return coralSession.getUserSubject().hasRole(getLinkRoot(data).getAdministrator());
        }
        catch(ProcessingException e)
        {
            log.error("Subject has no rights to view this screen");
            return false;
        }
    }
}


