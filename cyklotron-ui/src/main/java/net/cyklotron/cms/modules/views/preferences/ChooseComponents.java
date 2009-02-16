/*
 */
package net.cyklotron.cms.modules.views.preferences;

import java.util.ArrayList;
import java.util.Arrays;

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
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class ChooseComponents extends BasePreferencesScreen
{
    protected SiteService siteService;
    
    public ChooseComponents(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SiteService siteService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        
        this.siteService = siteService;
    }
    /* 
     * (overriden)
     */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        Parameters sys = preferencesService.getSystemPreferences(coralSession);
        String[] components = sys.getStrings("globalComponents");
        templatingContext.put("components", Arrays.asList(components));
        SiteResource[] sites = siteService.getSites(coralSession);
        ArrayList names = new ArrayList(sites.length);
        for (int i = 0; i < sites.length; i++)
        {
            names.add(sites[i].getName());
        }
        templatingContext.put("sites", names);
        templatingContext.put("data_site", sys.get("globalComponentsData",null));
    }
}
