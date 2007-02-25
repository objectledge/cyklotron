package net.cyklotron.cms.modules.actions.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumConstants;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseForumAction.java,v 1.3 2007-02-25 14:14:11 pablo Exp $
 */
public abstract class BaseForumAction
    extends BaseCMSAction
    implements ForumConstants
{
    protected ForumService forumService;
    
    protected WorkflowService workflowService;

    
    public BaseForumAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, ForumService forumService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory);
        this.forumService = forumService;
        this.workflowService = workflowService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("forum"))
        {
            logger.debug("Application 'forum' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            return coralSession.getUserSubject().hasRole(forumService.getForum(coralSession, getSite(context)).getAdministrator());
        }
        catch(Exception e)
        {
            logger.error("Subject has no rights to view this screen");
            return false;
        }
    }
}


