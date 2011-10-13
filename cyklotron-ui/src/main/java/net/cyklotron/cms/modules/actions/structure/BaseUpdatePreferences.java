package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public abstract class BaseUpdatePreferences 
    extends BaseStructureAction
{
    protected PreferencesService preferencesService;

	/** site service */
	protected SiteService siteService;

    private final ComponentDataCacheService componentDataCacheService;
    
    public BaseUpdatePreferences(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService,
        ComponentDataCacheService componentDataCacheService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.preferencesService = preferencesService;
		this.siteService = siteService;
        this.componentDataCacheService = componentDataCacheService;
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
            conf = preferencesService.getSystemPreferences(coralSession);
        }

        String scope = parameters.get("scope",null);
        conf = getScopedConfig(conf, node, scope, coralSession);

        try
        {
            modifyNodePreferences(context, conf, parameters, coralSession);
        }
        catch(ProcessingException e)
        {
            throw e;
        }
        
        componentDataCacheService.clearCachedData(node, scope);

        if(node != null)
        {
            node.update();
        }
    }

    public Parameters getScopedConfig(Parameters conf,
        NavigationNodeResource node, String scope, CoralSession coralSession)
    throws ProcessingException
    {
        if(scope != null)
        {
            Parameters combinedConf;
            // get component app and class to create it's config scope
            if(node != null)
            {
                combinedConf = preferencesService.getCombinedNodePreferences(coralSession, node);
            }
            else
            {
                combinedConf = preferencesService.getSystemPreferences(coralSession); 
            }
            String app = CmsComponentData.getParameter(combinedConf,"component."+scope+".app",null);
            String comp = CmsComponentData.getParameter(combinedConf,"component."+scope+".class",null);

            conf = conf.getChild("component."+scope+".config."+app+"."+comp.replace(',','.')+".");
        }
        return conf;
    }

    public abstract void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
    throws ProcessingException;
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData(context);
        if(cmsData.getNode() != null) 
        {
            return cmsData.getNode().canModify(coralSession, coralSession.getUserSubject());
        }
        else
        {
            // check for permissions needed to configure global components
            return checkAdministrator(context);
        }
    }
}
