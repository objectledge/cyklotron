package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class NodePreferences
    extends BaseStructureScreen
{


    public NodePreferences(org.objectledge.context.Context context, Logger logger,
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
        try
        {
            String scope = parameters.get("component_instance",null);
            CmsData cmsData = getCmsData();
            Parameters conf;
            Parameters combinedConf;
            NavigationNodeResource node = cmsData.getNode();
            if(node != null)
            {
                conf = preferencesService.getNodePreferences(node);
                combinedConf = preferencesService.getCombinedNodePreferences(node);
            }
            else
            {
                conf = preferencesService.getSystemPreferences();
                combinedConf = conf;
            }
            if(scope != null)
            {
                String app = combinedConf.get("component."+scope+".app");
                String comp = combinedConf.get("component."+scope+".class");
                conf = conf.getChild("component."+scope+
                    ".config."+app+"."+comp.replace(',','.')+".");
                templatingContext.put("scope", scope);
            }
            templatingContext.put("config", conf.toString());
            
            List parentList = new ArrayList();
            Map parentConf = new HashMap();
            if(node != null)
            {            
                Resource p = node.getParent();
                while(p instanceof NavigationNodeResource)
                {
                    parentList.add(0, p);
                    conf = preferencesService.getNodePreferences((NavigationNodeResource)p);
                    if(scope != null)
                    {
                        conf = conf.getChild(scope+".");
                    }
                    parentConf.put(p, conf);
                    p = p.getParent();
                }
                templatingContext.put("top", p);
            }
            templatingContext.put("parent_list", parentList);
            templatingContext.put("parent_conf", parentConf);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load configuration", e);
        }
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
            // check permissions required to configure global components
            return checkAdministrator(coralSession);
        }
    }
}
