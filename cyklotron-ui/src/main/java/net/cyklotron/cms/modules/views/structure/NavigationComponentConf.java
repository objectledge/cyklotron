package net.cyklotron.cms.modules.views.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;


/**
 * Screen for all navigation component's configuration.
 */
public class NavigationComponentConf extends BaseStructureScreen
{
    
    public NavigationComponentConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        NavigationConfiguration naviConf = new NavigationConfiguration(componentConfig);

        String navigationRootPath = naviConf.getRootPath();
        
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site selected");
        }
        try
        {
            Resource homePage = structureService.getRootNode(coralSession, site);
            Resource homePageParent = homePage.getParent();
            Resource root;
            if(!navigationRootPath.equals(""))
            {
                Resource[] temp = coralSession.getStore()
                                .getResourceByPath(homePageParent.getPath()+navigationRootPath);
                root = temp[0];
            }
            else
            {
                root = homePage;
            }
            templatingContext.put("navi_root", root);
        }
        catch(StructureException e)
        {
            throw new ProcessingException("failed to lookup site root", e);
        }

        templatingContext.put("navi_conf", naviConf);
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(context, coralSession.getUserSubject());
        }
        else
        {
            return checkAdministrator(coralSession);
        }
    }
}
