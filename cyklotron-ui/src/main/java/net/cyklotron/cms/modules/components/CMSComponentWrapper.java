package net.cyklotron.cms.modules.components;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.IntegrationService;

/**
 * Packaging component for all CMS components. It is being used to call
 * every CMS component rendering. It will allow to build a portal like
 * layouts with configurable component placing.
 */
public class CMSComponentWrapper
    extends BaseCMSComponent
{
    public static String INSTANCE_PARAM_KEY = "instance";

    private IntegrationService integrationService;

    
    public CMSComponentWrapper(Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, 
        IntegrationService integrationService)
    {
        super(context, logger, templating, cmsDataFactory);
        this.integrationService = integrationService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, 
        TemplatingContext templatingContext, HttpContext httpContext,
        I18nContext i18nContext, CoralSession coralSession)
    throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        
        // 0. start new component
        String instanceName = (String)(templatingContext.get(INSTANCE_PARAM_KEY));
        // WARN: take component main config from context
        //       - may be passed via $component.include() parameters
        String compApp = (String)(templatingContext.get("app"));
        String compClass = (String)(templatingContext.get("class"));

        CmsComponentData componentData = null;
        if(compApp != null && compClass != null)
        {
            componentData = cmsData.nextComponent(instanceName, compApp, compClass);
        }
        else
        {
            componentData = cmsData.nextComponent(instanceName);
        }

        // 1. execute different modes
        String mode = cmsData.getBrowseMode();
        if(mode.equals("edit"))
        {
            prepareEditMode(templatingContext, cmsData, componentData, coralSession);
        }
        else if(mode.equals("import"))
        {
            prepareImportExportMode(templatingContext, cmsData, componentData, coralSession);
        }
        else if(mode.equals("export"))
        {
            prepareImportExportMode(templatingContext, cmsData, componentData, coralSession);
        }
        //else if(mode.equals("emergency")) {}
        //else if(mode.equals(CmsData.BROWSE_MODE_ADMINISTER)) { /* WARN: SHOULD NEVER HAPPEN */ }
        //else if(mode.equals(CmsData.BROWSE_MODE_BROWSE)) { /* WARN: Just do nothing */ }
    }
    
    private void prepareEditMode(TemplatingContext templatingContext, CmsData cmsData, CmsComponentData componentData,
        CoralSession coralSession)
    throws ProcessingException
    {
        
        // Check if current subject is permitted to configure this component
        // TODO: Add permission information to component registry!!!
        //     Add a permission name to component definition in registry
        //
        /*
        if(coralSession.getUserSubject().hasPermission(node, permission))
        {
            editMode = new Boolean(true);
        }
        else
        {
            editMode = new Boolean(false);
        }
        */

        // Get a wrapped component's configurator class name
        ComponentResource componentRes =
            integrationService.getComponent(coralSession, componentData.getApp(), componentData.getClazz());

        if(componentRes == null)
        {
            componentData.error("Cannot get component description from integration registry", null);
        }
        else
        {
            String componentConfigApp = componentData.getApp();
            String componentConfigView = componentRes.getConfigurationView();
            if(componentConfigView == null || componentConfigView.length() == 0)
            {
                componentConfigApp = "cms";
                componentConfigView = "structure.NodePreferences";
            }
            else
            {
                componentConfigView = componentConfigView.replace(',','.');
            }
            templatingContext.put("component_config_app", componentConfigApp);
            templatingContext.put("component_config_view", componentConfigView);
        }
    }

    private void prepareImportExportMode(TemplatingContext templatingContext, CmsData cmsData, CmsComponentData componentData,
        CoralSession coralSession)
    throws ProcessingException
    {
        String SOURCE_VIEW_KEY = "component_has_source_view";
        
        
        // Get a wrapped component's configurator class name
        ComponentResource componentRes =
            integrationService.getComponent(coralSession, componentData.getApp(), componentData.getClazz());

        if(componentRes == null)
        {
            templatingContext.put(SOURCE_VIEW_KEY, new Boolean(false));
        }
        else
        {
            String componentImportApp = componentData.getApp();
            String componentImportView = componentRes.getAggregationSourceView();
            if(componentImportView == null || componentImportView.length() == 0)
            {
                templatingContext.put(SOURCE_VIEW_KEY, new Boolean(false));
            }
            else
            {
                templatingContext.put(SOURCE_VIEW_KEY, new Boolean(true));
                
                templatingContext.put("component_source_app", componentImportApp);
                templatingContext.put("component_source_view", componentImportView);
            }
        }
    }
}
