package net.cyklotron.cms.modules.actions.link;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseLinkAction.java,v 1.4 2005-02-10 17:50:16 rafal Exp $
 */
public abstract class BaseLinkAction
    extends BaseCMSAction
{
    /** link service */
    protected LinkService linkService;

    /** workflow service */
    protected WorkflowService workflowService;

    public BaseLinkAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, LinkService linkService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory);
        this.linkService = linkService;
        this.workflowService = workflowService;
    }

    public LinkRootResource getLinkRoot(Context context, CoralSession coralSession, Parameters parameters)
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
            return linkService.getLinkRoot(coralSession, site);
        }
        catch(LinkException e)
        {
            throw new ProcessingException("failed to lookup links root");
        }
    }

    public boolean checkAccessRights(Context context)
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            return coralSession.getUserSubject().hasRole(getLinkRoot(context, coralSession, parameters).getAdministrator());
        }
        catch(ProcessingException e)
        {
            logger.error("Subject has no rights to view this screen");
            return false;
        }
    }
}


