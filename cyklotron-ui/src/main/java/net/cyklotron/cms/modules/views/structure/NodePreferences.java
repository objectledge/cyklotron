package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.labeo.Labeo;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class NodePreferences
    extends BaseStructureScreen
{
    protected PreferencesService preferencesService;

    public NodePreferences()
    {
        preferencesService = (PreferencesService)Labeo.getBroker().
            getService(PreferencesService.SERVICE_NAME);
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
                conf = conf.getSubset("component."+scope+
                    ".config."+app+"."+comp.replace(',','.')+".");
                templatingContext.put("scope", scope);
            }
            templatingContext.put("config", conf.getContents());
            
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
                        conf = conf.getSubset(scope+".");
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
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        { 
            return cmsData.getNode().canModify(coralSession.getUserSubject());
        }
        else
        {
            // check permissions required to configure global components
            return checkAdministrator(coralSession);
        }
    }
}
