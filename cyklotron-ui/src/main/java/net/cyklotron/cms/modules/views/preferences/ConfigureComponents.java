/*
 */
package net.cyklotron.cms.modules.views.preferences;

import java.util.Arrays;

import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class ConfigureComponents extends BasePreferencesScreen
{
    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) throws ProcessingException
    {
        Parameters sys = preferencesService.getSystemPreferences();
        String[] components = sys.getStrings("globalComponents");
        templatingContext.put("components", Arrays.asList(components));
        cmsDataFactory.getCmsData(context).setBrowseModeOverride(CmsConstants.BROWSE_MODE_EDIT);
    }
}
