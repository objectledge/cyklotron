package net.cyklotron.cms.modules.views.structure;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationConfiguration;
import net.cyklotron.cms.structure.StructureException;


/**
 * Screen for all navigation component's configuration.
 */
public class NavigationComponentConf extends BaseStructureScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
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
            Resource homePage = structureService.getRootNode(site);
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
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(coralSession.getUserSubject());
        }
        else
        {
            return checkAdministrator(coralSession);
        }
    }
}
