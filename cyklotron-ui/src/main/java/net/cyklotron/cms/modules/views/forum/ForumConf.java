package net.cyklotron.cms.modules.views.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 * The discussion list screen class.
 */
public class ForumConf
    extends BaseForumScreen
{
    
    public ForumConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, ForumService forumService,
        WorkflowService workflowService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, forumService,
                        workflowService);
        // TODO Auto-generated constructor stub
    }
    
    //TODO what to do with it!!!
    /**
    public Screen route(RunData data)
        throws NotFoundException, ProcessingException
    {
        try
        {
            CmsData cmsData = getCmsData();
            NavigationNodeResource node = cmsData.getNode();
            Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
            String instance = parameters.get("component_instance","");
            
            httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
            httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
            if(node != null)
            {
                httpContext.setSessionAttribute(COMPONENT_NODE, node.getIdObject());
            }

            SiteResource site = cmsData.getSite();
            if(site == null)
            {
                site = cmsData.getGlobalComponentsDataSite();          
            }
            if(site == null)
            {
                throw new ProcessingException("No site selected");
            }
            ForumResource forumResource = forumService.getForum(site);
            parameters.set("fid",forumResource.getIdString());
            long did = componentConfig.get("did").asLong(-1);
            parameters.set("did",did);
            mvcContext.setView("forum,DiscussionList");
            return (Screen)data.getScreenAssembler();
        }
        catch(ForumException e)
        {
            throw new ProcessingException("ForumException",e);
        }
        
    }
    */

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode();
        String instance = parameters.get("component_instance","");
        httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
        httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
        httpContext.setSessionAttribute(COMPONENT_NODE,node.getIdObject());
    }
}
