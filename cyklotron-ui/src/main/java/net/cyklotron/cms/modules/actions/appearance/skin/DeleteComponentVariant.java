package net.cyklotron.cms.modules.actions.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.ComponentStateResource;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DeleteComponentVariant.java,v 1.1 2005-01-24 04:34:04 pablo Exp $
 */
public class DeleteComponentVariant extends BaseAppearanceAction
{
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        SiteResource site = getSite(context);
        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        String component = parameters.get("compName");
        String variant =
            parameters.get("variant","Default");
        ApplicationResource appRes = integrationService.getApplication(app);
        ComponentResource compRes =
            integrationService.getComponent(appRes, component);
        try
        {
            ComponentStateResource[] states =
                integrationService.getComponentStates(compRes);
            if (states.length > 0)
            {
                for (int i = 0; i < states.length; i++)
                {
                    if(skinService.hasComponentTemplate(
                        site,
                        skin,
                        compRes.getApplicationName(),
                        compRes.getComponentName(),
                        variant,
                        states[i].getName()))
                    {
                        skinService.deleteComponentTemplate(
                            site,
                            skin,
                            compRes.getApplicationName(),
                            compRes.getComponentName(),
                            variant,
                            states[i].getName());
                    }
                }
            }
            else
            {
                if(skinService.hasComponentTemplate(
                    site,
                    skin,
                    compRes.getApplicationName(),
                    compRes.getComponentName(),
                    variant,
                    "Default"))
                {
                    skinService.deleteComponentTemplate(
                        site,
                        skin,
                        compRes.getApplicationName(),
                        compRes.getComponentName(),
                        variant,
                        "Default");
                }
            }
            skinService.deleteComponentVariant(
                site,
                skin,
                compRes.getApplicationName(),
                compRes.getComponentName(),
                variant);
            }
        catch (Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if (context.containsKey("result"))
        {
            data.setView("appearance,skin,DeleteComponentVariant");
        }
        else
        {
            templatingContext.put("result", "deleted_successfully");
        }
    }
}
