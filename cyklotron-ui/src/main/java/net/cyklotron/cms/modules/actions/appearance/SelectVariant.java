package net.cyklotron.cms.modules.actions.appearance;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class SelectVariant
    extends BaseAppearanceAction
{
    protected PreferencesService preferencesService;

    protected SiteService siteService;

    
    
    public SelectVariant(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService,
        PreferencesService preferencesService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        this.preferencesService = preferencesService;
        this.siteService = siteService;
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData(context);
        SiteResource site = cmsData.getSite();
        NavigationNodeResource node = cmsData.getNode();
        Parameters preferences;
        if(node != null)
        {
            preferences = preferencesService.getCombinedNodePreferences(coralSession, node);
        }
        else
        {
            preferences = preferencesService.getSystemPreferences(coralSession);
            String dataSite = preferences.get("globalComponentsData","");
            try
            {
                site = siteService.getSite(coralSession, dataSite);
            }
            catch(SiteException e)
            {
                throw new ProcessingException("failed to lookup global components data site");
            }
        }

        String instance = parameters.get("component_instance");
		String app = preferences.get("component."+instance+".app",null);
		String component = preferences.get("component."+instance+".class",null);
		String currentVariant = preferences.get("component."+instance+".variant."+
			app+"."+component.replace(',','.'),"Default");

        String newVariant  = parameters.get("selected","Default");

        if(currentVariant.equals(newVariant))
        {
            return;
        }

        ComponentVariantResource[] variants;
        try
        {
            String skin = skinService.getCurrentSkin(coralSession, site);
            variants = skinService.getComponentVariants(coralSession, site, skin, app, component);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve variant information");
        }

        boolean variantExists = false;
        for(int i=0; i<variants.length; i++)
        {
            if(variants[i].getName().equals(newVariant))
            {
                variantExists = true;
                break;
            }
        }

        if(variantExists)
        {
            if(cmsData.getNode() != null)
            {
                preferences = preferencesService.getNodePreferences(node);
            }
            preferences.set("component."+instance+".variant."+
	       		app+"."+component.replace(',','.'), newVariant);
            if(cmsData.getNode() != null)
            {
                node.update();
            }
        }
        else
        {
            throw new ProcessingException("cannot set a non existant variant");
        }
    }
}
