package net.cyklotron.cms.modules.views.forum;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.CreationTimeComparator;
import org.objectledge.coral.table.comparator.CreatorNameComparator;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.captcha.CaptchaService.CaptchaApiVersion;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.ForumService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
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
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws ProcessingException
    {
		try
        {
            CmsData cmsData = getCmsData();
			SiteResource site = cmsData.getSite();
            if(site == null)
            {
                site = cmsData.getGlobalComponentsDataSite();          
            }
            if(site == null)
            {
                throw new ProcessingException("No site selected");
            }
            ForumResource forum = forumService.getForum(coralSession, site);
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            // discussions
            Resource[] res = coralSession.getStore().getResource(forum, "discussions");
            if(res.length != 1)
            {
                throw new ProcessingException("discussions node not found in "+forum.getPath());
            }
            Resource[] discussions = coralSession.getStore().getResource(res[0]);
            templatingContext.put("discussions", discussions);
            TableState state = tableStateManager.getState(context, "cms:screens:forum,DiscussionList:discussions");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(discussions), columns);
            templatingContext.put("discussions_table", new TableTool(state, null, model));
            
            // comments
            res = coralSession.getStore().getResource(forum, "comments");
            if(res.length != 1)
            {
                throw new ProcessingException("comments node not found "+forum.getPath());
            }
            Resource[] comments = coralSession.getStore().getResource(res[0]);
            templatingContext.put("comments", comments);
            state = tableStateManager.getState(context, "cms:screens:forum,DiscussionList:comments");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            model = new ListTableModel(Arrays.asList(comments), columns);
            templatingContext.put("comments_table", new TableTool(state, null, model));

			Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
			templatingContext.put("componentConfig", componentConfig);
            templatingContext.put("available_recaptcha_api_version",
                Arrays.asList(CaptchaApiVersion.values()));
			
			long dId = componentConfig.getLong("did",-1);
	        if(dId != -1)
	        {
				try
				{
					Resource selected = coralSession.getStore().getResource(dId);
					templatingContext.put("selected",selected);
				}
				catch(EntityDoesNotExistException e)
				{
					//non existing discusson may be configured
				}
	        }
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize column data", e);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to prepare view", e);
        }
    }
	
    /**
     * {@inheritDoc}
     */
	/**
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
    */
}
