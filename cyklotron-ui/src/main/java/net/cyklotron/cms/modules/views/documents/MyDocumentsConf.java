package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class MyDocumentsConf
    extends BaseCMSScreen
{

    public MyDocumentsConf(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        templatingContext.put("config", componentConfig);
        addResource("more_node", componentConfig, coralSession, templatingContext);
        addResource("include_query", componentConfig, coralSession, templatingContext);
        addResource("exclude_query", componentConfig, coralSession, templatingContext);
    }

    private void addResource(String name, Parameters config, CoralSession coralSession,
        TemplatingContext templatingContext)
    {
        long includeQueryId = config.getLong(name + "_id", -1l);
        if(includeQueryId != -1l)
        {
            try
            {
                Resource res = coralSession.getStore().getResource(includeQueryId);
                templatingContext.put(name, res);
                if(res instanceof NavigationNodeResource)
                {
                    templatingContext.put(name + "_path",
                        ((NavigationNodeResource)res).getSitePath());
                }
            }
            catch(EntityDoesNotExistException e)
            {
                // welp, resource must have been deleted
            }
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(coralSession, coralSession.getUserSubject());
        }
        else
        {
            return checkAdministrator(coralSession);
        }
    }
}
