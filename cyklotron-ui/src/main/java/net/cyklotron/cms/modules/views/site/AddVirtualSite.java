package net.cyklotron.cms.modules.views.site;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 *
 */
public class AddVirtualSite
    extends BaseSiteScreen
{
    
    public AddVirtualSite(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SiteService siteService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, siteService);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            NavigationNodeResource homePage = getHomePage();
            Resource structure = homePage.getParent();
            templatingContext.put("root", structure);

            if(!templatingContext.containsKey("default_node_path"))
            {
                templatingContext.put("default_node_path", homePage.getSitePath());
                templatingContext.put("default_node", homePage);
            }
            else
            {
                String defaultNodePath = (String)templatingContext.get("default_node_path");
                Resource[] res = coralSession.getStore().getResourceByPath(structure.getPath()+
                                                                   defaultNodePath);
                if(res.length == 1)
                {
                    templatingContext.put("default_node", res[0]);
                }
            }
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            throw new ProcessingException("failed to lookup site information", e);
        }
    }
}
