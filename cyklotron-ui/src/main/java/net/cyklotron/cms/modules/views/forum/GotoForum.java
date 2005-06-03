package net.cyklotron.cms.modules.views.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * The forum search result screen class.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: GotoForum.java,v 1.1 2005-06-03 07:59:52 pablo Exp $
 */
public class GotoForum
    extends BaseForumScreen
{
    
    
    public GotoForum(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        ForumService forumService, WorkflowService workflowService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, forumService,
                        workflowService);
        
    }
    
    
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.views.BaseCMSScreen#process(org.objectledge.parameters.Parameters, org.objectledge.web.mvc.MVCContext, org.objectledge.templating.TemplatingContext, org.objectledge.web.HttpContext, org.objectledge.i18n.I18nContext, org.objectledge.coral.session.CoralSession)
     */
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws ProcessingException
    {
        LinkTool link = (LinkTool)templatingContext.get("link");
        long rid = parameters.getLong("res_id", -1);
        if(rid == -1)
        {
            throw new ProcessingException("Resource id not found");
        }
        try
        {
            Resource resource = coralSession.getStore().getResource(rid);
            SiteResource site = CmsTool.getSite(resource);
            if(resource instanceof ForumResource)
            {
                link = link.unset("x").view("forum,DiscussionList")
                    .set("fid",rid).set("site_id",site.getId());    
            }
            if(resource instanceof DiscussionResource)
            {
                DiscussionResource discussion = (DiscussionResource)resource;
                link = link.unset("x").view("forum,MessageList")
                    .set("fid",discussion.getForum().getId())
                    .set("did",rid)
                    .set("site_id",site.getId());    
            }
            if(resource instanceof MessageResource)
            {
                MessageResource message = (MessageResource)resource; 
                link = link.unset("x").view("forum,Message")
                    .set("fid",message.getDiscussion().getForum().getId())
                    .set("did",message.getDiscussion().getId())
                    .set("mid",rid).set("site_id",site.getId());    
            }
            httpContext.sendRedirect(link.toString());
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured during redirecting...",e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
	
    /**
     * @{inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    } 
    
}
