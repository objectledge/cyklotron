package net.cyklotron.cms.modules.views.appearance.style;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 *
 * @author <a href="mailto:mover@ngo.pl">Michal Mach</a>
 */
public class AddStyle
    extends BaseAppearanceScreen
{
    
    public AddStyle(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            Resource root = styleService.getStyleRoot(coralSession, site);
            //String rootId = ""+root.getId();
            Resource[] resources = coralSession.getStore().getResource(root);
            if(resources.length != 1)
            {
                throw new ProcessingException("Default style not found nor unique");
            }
            templatingContext.put("style_id", resources[0].getIdString());
        }
        catch (Exception e)
        {
            logger.error("Error occured while fetching styleRoot ",e);
            throw new ProcessingException("Error occured while fetching styleRoot ",e);
        }
    }
}
