package net.cyklotron.cms.modules.views.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumException;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
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
        
    }
    
    /**
     * {@inheritDoc}
     */
    public String route(String thisViewName)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext =
            TemplatingContext.getTemplatingContext(context);
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
            ForumResource forumResource = forumService.getForum(coralSession, site);
            parameters.set("fid",forumResource.getIdString());
            long did = componentConfig.getLong("did",-1);
            parameters.set("did",did);
            return "forum.DiscussionList";
        }
        catch(ForumException e)
        {
            throw new ProcessingException("ForumException",e);
        }
        
    }

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
