package net.cyklotron.cms.modules.views;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.picocontainer.Parameter;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * The base screen assember for CMS applications.
 */
public abstract class BaseCMSScreen
	extends BaseCoralView
{
    protected PreferencesService preferencesService;
    
    protected Logger logger;
    
    protected CmsDataFactory cmsDataFactory;
    
    public BaseCMSScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory)
    {
        super(context);
        this.logger= logger;
        this.preferencesService = preferencesService;
        this.cmsDataFactory = cmsDataFactory;
    }
    
    
    
    
    /** WARN: This method is useful for component configuration screens.
     *  They should call it in order to get displayed configuration.
     *
     * TODO: perform it using CmsData and others
     * @param templatingContext TODO
     */
    public Parameters prepareComponentConfig(Parameters parameters, TemplatingContext templatingContext)
	throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        String instanceName = parameters.get("component_instance","");
        templatingContext.put("instance", instanceName);
        Parameters nodeConfig = cmsData.getPreferences();
        String app = nodeConfig.get("component."+instanceName+".app");
        String comp = nodeConfig.get("component."+instanceName+".class");
        Parameters componentConfig = nodeConfig.getChild("component."+instanceName+
                                                             ".config."+app+"."+comp.replace(',','.')+".");
        Parameters config = new DefaultParameters();
        config.set("app", app);
        config.set("class", comp);
        config.add(componentConfig, true);
        return config;
    }
    
    /** WARN: This method is useful for screen configuration screens.
     *  They should call it in order to get displayed configuration.
     *
     * TODO: perform it using CmsData and others
     */
	public Parameters prepareScreenConfig(RunData data)
            throws ProcessingException
    {
        NavigationNodeResource node = getNode();
        Parameters nodeConfig = preferencesService.getCombinedNodePreferences(node);
        String app = nodeConfig.get("screen.app");
        String screen = nodeConfig.get("screen.class");
        
        Parameters screenConfig = nodeConfig.getSubset("screen.config."
                                                          +app+"."+screen.replace(',','.')+".");
        Parameters config = new BaseParameters();
        config.set("app", app);
        config.set("class", screen);
        return new BaseParameters(config.merge(screenConfig, true));
    }
    
    public CmsData getCmsData()
        throws ProcessingException
    {
        return cmsDataFactory.getCmsData(context);
    }
    
    /** TODO: Remove after CmsData is widely used */
    public boolean isNodeDefined()
        throws ProcessingException
    {
        return getCmsData().isNodeDefined();
    }
    
    /** TODO: Remove after CmsData is widely used */
    public NavigationNodeResource getNode()
        throws ProcessingException
    {
        return getCmsData().getNode();
    }
    
    /** TODO: Remove after CmsData is widely used */
    public SiteResource getSite()
        throws ProcessingException
    {
        return getCmsData().getSite();
    }
    
    /** TODO: Remove after CmsData is widely used */
    public NavigationNodeResource getHomePage()
        throws ProcessingException
    {
        return getCmsData().getHomePage();
    }
    
    
    /**
     * Checks if the current user has the specific permission on the current
     * node.
     */
    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        try
        {
            NavigationNodeResource node = getNode();
            Permission permission = coralSession.getSecurity().
                getUniquePermission(permissionName);
            return coralSession.getUserSubject().hasPermission(node, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }

    /**
     * Checks if the current user has administrative privileges on the current
     * site.
     */
    public boolean checkAdministrator(CoralSession coralSession)
        throws ProcessingException
    {
        return getCmsData().checkAdministrator(coralSession);
    }
}
