package net.cyklotron.cms.modules.actions.structure;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

public abstract class BaseUpdatePreferences extends BaseStructureAction
{
    protected PreferencesService preferencesService;

	/** site service */
	protected SiteService siteService;

    public BaseUpdatePreferences()
    {
        preferencesService = (PreferencesService)broker.
            getService(PreferencesService.SERVICE_NAME);
		siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode(context);
        Parameters conf;
        if(node != null)
        {
            conf = preferencesService.getNodePreferences(node);
        }
        else
        {
            conf = preferencesService.getSystemPreferences();
        }

        String scope = parameters.get("scope",null);
        conf = getScopedConfig(data, conf, node, scope);

        boolean clear = parameters.getBoolean("clearConfig", false);
        if(clear)
        {
            conf.clear();
        }
        
        try
        {
            modifyNodePreferences(data, conf);
        }
        catch(ProcessingException e)
        {
            throw e;
        }

        if(node != null)
        {
            node.update(coralSession.getUserSubject());
        }
    }

    public Parameters getScopedConfig(RunData data, Parameters conf,
        NavigationNodeResource node, String scope)
    throws ProcessingException
    {
        if(scope != null)
        {
            Parameters combinedConf;
            // get component app and class to create it's config scope
            if(node != null)
            {
                combinedConf = preferencesService.getCombinedNodePreferences(node);
            }
            else
            {
                combinedConf = preferencesService.getSystemPreferences(); 
            }
            String app = combinedConf.get("component."+scope+".app");
            String comp = combinedConf.get("component."+scope+".class");

            conf = conf.getSubset("component."+scope+".config."+app+"."+comp.replace(',','.')+".");
        }
        return conf;
    }

    public abstract void modifyNodePreferences(RunData data, Parameters conf)
    throws ProcessingException;
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData(context);
        if(cmsData.getNode() != null) 
        {
            return cmsData.getNode().canModify(coralSession.getUserSubject());
        }
        else
        {
            // check for permissions needed to configure global components
            return checkAdministrator(context, coralSession);
        }
    }
}
