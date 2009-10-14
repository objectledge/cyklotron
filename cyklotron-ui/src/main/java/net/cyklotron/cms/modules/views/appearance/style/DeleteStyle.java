package net.cyklotron.cms.modules.views.appearance.style;

import java.util.Arrays;

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
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 */
public class DeleteStyle
    extends BaseAppearanceScreen
{
    
    public DeleteStyle(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long styleId = parameters.getLong("style_id", -1);
        if(styleId == -1)
        {
            throw new ProcessingException("style id couldn't be found");
        }
        try 
        {
            StyleResource resource =  StyleResourceImpl.getStyleResource(coralSession, styleId);
            Resource[] children = coralSession.getStore().getResource(resource);
            templatingContext.put("style",resource);
            templatingContext.put("referers", styleService.getReferringNodes(coralSession, resource));
            templatingContext.put("children", Arrays.asList(children));
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to load information", e);
        }
    }
}
