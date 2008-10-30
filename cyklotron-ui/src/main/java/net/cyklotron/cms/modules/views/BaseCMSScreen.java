package net.cyklotron.cms.modules.views;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.security.SecurityChecking;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsConstants;
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
    implements SecurityChecking, CmsConstants
{
    protected PreferencesService preferencesService;
    
    protected Logger logger;
    
    protected CmsDataFactory cmsDataFactory;
    
    protected TableStateManager tableStateManager;
    
    public BaseCMSScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager)
    {
        super(context);
        this.logger= logger;
        this.preferencesService = preferencesService;
        this.cmsDataFactory = cmsDataFactory;
        this.tableStateManager = tableStateManager;
    }
    
    public void process(Parameters parameters, TemplatingContext templatingContext,
        MVCContext mvcContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        process(parameters, mvcContext, templatingContext, httpContext, i18nContext,
            coralSession);
    }
    
    /**
     * To be implemented in views.
     * @param parameters the parameters.
     * @param mvcContext the mvcContext
     * @param templatingContext the templating context.
     * @param i18nContext the i18n context.
     * @param coralSession the coral session.
     */    
    public abstract void process(Parameters parameters, MVCContext mvcContext, 
                                 TemplatingContext templatingContext, HttpContext httpContext, 
                                 I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException;
    
    /** WARN: This method is useful for component configuration screens.
     *  They should call it in order to get displayed configuration.
     *
     * TODO: perform it using CmsData and others
     * @param templatingContext the templatingContext
     */
    public Parameters prepareComponentConfig(Parameters parameters, TemplatingContext templatingContext)
	throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        String instanceName = parameters.get("component_instance","");
        templatingContext.put("instance", instanceName);
        Parameters nodeConfig = cmsData.getPreferences();
        String app = CmsComponentData.getParameter(nodeConfig,"component."+instanceName+".app",null);
        String comp = CmsComponentData.getParameter(nodeConfig,"component."+instanceName+".class",null);
        Parameters componentConfig = nodeConfig.getChild("component."+instanceName+
                                                             ".config."+app+"."+comp.replace(',','.')+".");
        Parameters config = new DefaultParameters();
        config.set("app", app);
        config.set("class", comp);
        config.add(componentConfig, true);
        return config;
    }
    
    /** This method is useful for screen configuration screens.
     *  They should call it in order to get displayed configuration.
     */
	public Parameters getScreenConfig()
            throws ProcessingException
    {
        return getCmsData().getEmbeddedScreenConfig();
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
    
    /**
     * @{inheritDoc}
     */
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }

    /**
     * @{inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return true;
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        return true;
    }

}
