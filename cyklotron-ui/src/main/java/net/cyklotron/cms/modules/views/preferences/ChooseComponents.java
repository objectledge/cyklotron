/*
 */
package net.cyklotron.cms.modules.views.preferences;

import java.util.ArrayList;
import java.util.Arrays;

import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class ChooseComponents extends BasePreferencesScreen
{
    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) throws ProcessingException
    {
        Parameters sys = preferencesService.getSystemPreferences();
        String[] components = sys.getStrings("globalComponents");
        templatingContext.put("components", Arrays.asList(components));
        SiteService siteService = (SiteService)data.getBroker().
            getService(SiteService.SERVICE_NAME);
        SiteResource[] sites = siteService.getSites();
        ArrayList names = new ArrayList(sites.length);
        for (int i = 0; i < sites.length; i++)
        {
            names.add(sites[i].getName());
        }
        templatingContext.put("sites", names);
        templatingContext.put("data_site", sys.get("globalComponentsData",null));
    }
}
