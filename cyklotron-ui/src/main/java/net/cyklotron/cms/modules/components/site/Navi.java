package net.cyklotron.cms.modules.components.site;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.components.BaseCMSComponent;
import net.cyklotron.cms.site.SiteService;

/**
 * Site UI navigation component.
 */
public class Navi
    extends BaseCMSComponent
{
    private SiteService siteService;

    public Navi()
    {
        ServiceBroker broker = Labeo.getBroker();
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        List sites = Arrays.asList(siteService.getSites());
        Collections.sort(sites, new NameComparator(i18nContext.getLocale()()));
        templatingContext.put("sites", sites);
    }
}
