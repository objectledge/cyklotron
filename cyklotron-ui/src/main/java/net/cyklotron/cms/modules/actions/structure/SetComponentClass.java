package net.cyklotron.cms.modules.actions.structure;

import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;

public class SetComponentClass
    extends BaseStructureAction
{
    protected PreferencesService preferencesService;

    protected IntegrationService integrationService;
    
    public SetComponentClass()
    {
        preferencesService = (PreferencesService)broker.
            getService(PreferencesService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.
            getService(IntegrationService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {

        try
        {
            String instance = parameters.get("instance");
            long componentId = parameters.getLong("component_id");
            ComponentResource component = (ComponentResource)coralSession.getStore().
                getResource(componentId);
            ApplicationResource application = integrationService.getApplication(component);
            Parameters preferences;
            CmsData cmsData = getCmsData(context);
            if(cmsData.getNode() != null)
            {
                preferences = preferencesService.getNodePreferences(cmsData.getNode());
            }
            else
            {
                preferences = preferencesService.getSystemPreferences();
            }
            preferences.set("component."+instance+".app", application.getApplicationName());
            preferences.set("component."+instance+".class", component.getComponentName());
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to set component class", e);
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData(context);
        if(cmsData.getNode() != null)
        {
            return getCmsData(context).getNode(context).canModify(coralSession.getUserSubject());
        }
        else
        {
            // privileges needed for configuring global components
            return checkAdministrator(context, coralSession);
        }
    }
}
