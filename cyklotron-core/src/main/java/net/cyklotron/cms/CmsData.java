package net.cyklotron.cms;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.AmbiguousParameterException;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.StructureUtil;

/**
 * A data object used to encapsulate CMS runtime data.
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsData.java,v 1.17 2008-10-30 17:43:47 rafal Exp $
 */
public class CmsData
    implements CmsConstants
{
    // services and utility objects
    /** The {@link Logger} */
    private Logger logger;
    /** structure service */
    private StructureService structureService;
    /** preferences service */
    private PreferencesService preferencesService;
    /** site service */
    private SiteService siteService;
    /** user manager */
    private UserManager userManager;
    /** integration manager */
    private IntegrationService integrationService;
    
    
    // attributes
    private Context context;
    
    private boolean adminMode;
    private String modeOverride;
    private SiteResource site;
    private NavigationNodeResource node;
    private NavigationNodeResource contentNode;
    private NavigationNodeResource homePage;
    private Parameters preferences;
    private Parameters systemPreferences;
    private SiteResource globalComponentsDataSite;
    private String skinName;

    private boolean skinNameOverridden;
    
    private Date date;

    private Set renderedComponentInstances;
    private CmsComponentData componentData;

    private UserData userData;
    
    // initialization ////////////////////////////////////////////////////////
    
    public CmsData(Context context, Logger logger, StructureService structureService, 
        PreferencesService preferencesService, SiteService siteService,
        UserManager userManager, IntegrationService integrationService)
    throws ProcessingException
    {
        this.context = context;
        this.logger = logger;
        this.structureService = structureService;
        this.preferencesService = preferencesService;
        this.siteService = siteService;
        this.userManager = userManager;
        this.integrationService = integrationService;
        Parameters parameters = RequestParameters.getRequestParameters(context);
        // get date from session
        HttpContext httpContext = HttpContext.getHttpContext(context);
        date = (Date)(httpContext.getSessionAttribute(CMS_DATE_KEY));
        if(date == null)
        {
            date = new Date();
        }
        // init cms data
        nodesSetup(parameters);
        preferencesSetup(parameters);            
        // skin preview
        if(site != null)
        {
            String previewKey = SkinService.PREVIEW_KEY_PREFIX + site.getName();
            String preview = (String)httpContext.getSessionAttribute(previewKey);
            if(preview != null)
            {
                skinName = preview;
            }
        }
    }

    private void nodesSetup(Parameters parameters)
        throws ProcessingException
    {
        CoralSession coralSession = getCoralSession(context);
        adminMode = true;
        modeOverride = null;
        if(parameters.isDefined("x"))
        {
            node = getNode(getPseudoUnique(parameters, "x", -1));
            adminMode = false;
        }

        // 1. Get the node
        if(node == null) // node not found using x parameter
        {
            // We are in admin mode - possible parameters are site_id and/or node_id or none
            long node_id = getPseudoUnique(parameters, "node_id",-1);
            if(node_id != -1)
            {
                node = getNode(node_id);
            }
            else
            {
                long site_id = getPseudoUnique(parameters, "site_id",-1);
                if(site_id != -1)
                {
                    try
                    {
                        Resource siteRes = coralSession.getStore().getResource(site_id);
                        if(siteRes instanceof SiteResource)
                        {
                            site = (SiteResource)siteRes;
                        }
                        else
                        {
                            logger.error("Resource with a given id="+site_id+" is not a site");
                        }
                    }
                    catch (EntityDoesNotExistException e)
                    {
                        logger.error("Site with id="+site_id+" does not exist", e);
                    }
                    node = getHomePage(coralSession, site);
                }
            }
        }

        if(node != null)
        {
            site = node.getSite();
            homePage = getHomePage(coralSession, site);
        }
        
        if(parameters.isDefined("doc_id"))
        {
            try
            {
                long doc_id = parameters.getLong("doc_id", -1L);
                contentNode = (DocumentNodeResource)coralSession.getStore().getResource(doc_id);
                // check if subject can view this node.
                if(!contentNode.canView(coralSession, coralSession.getUserSubject(), getDate()))
                {
                    contentNode = node;
                }
            }
            catch(EntityDoesNotExistException e)
            {
                contentNode = node;
            }
        }
        else
        {
            contentNode = node;
        }
    }
    
    private long getPseudoUnique(Parameters parameters, String name, long defaultValue)
    {
        long[] values = parameters.getLongs(name);
        if(values.length == 0)
        {
            return defaultValue;
        }
        if(values.length > 1)
        {
            logger.warn(name+" has multiple values");
        }
        long v = values[0];
        for(int i = 1; i < values.length; i++) 
        {
            if(values[i] != v)
            {
                throw new AmbiguousParameterException(name+" has multiple different values");
            }
        }
        return v;
    }

    protected void preferencesSetup(Parameters parameters)
        throws ProcessingException
    {
        systemPreferences = preferencesService.getSystemPreferences(getCoralSession(context)); 
        if(node != null)
        {
            preferences = preferencesService.getCombinedNodePreferences(getCoralSession(context),node);
        }
        else
        {
            preferences = systemPreferences;
        }
        String globalComponentsDataSiteName = systemPreferences.get("globalComponentsData",null);
        if(globalComponentsDataSiteName != null)
        {
            try
            {
                globalComponentsDataSite = siteService.getSite(getCoralSession(context),globalComponentsDataSiteName); 
            }
            catch(SiteException e)
            {
                throw new ProcessingException("globalComponentsData is set to noexistent site "+globalComponentsDataSiteName);
            }
        }
        if(parameters.isDefined(CmsConstants.SKIN_OVERRIDE))
        {
            skinName = parameters.get(CmsConstants.SKIN_OVERRIDE);
            skinNameOverridden = true;
        } 
        else
        {
            skinName = preferences.get("site.skin","default");
            skinNameOverridden = false;
        }
    }
    
    // public interface    // ///////////////////////////////////////////////////////

    /**
     * Returns current CMS time.
     */
    public Date getDate()
    {
        // secure date from external modifications
        return new Date(date.getTime());
    }

    /**
     * Return the user data.
     *
     * @return the subject.
     */
    public UserData getUserData()
    {
        if(userData == null)
        {
            userData = new UserData(context, logger, preferencesService, userManager, null);
        }
        return userData;
    }    


    /**
     * Returns currently browsed, edited site.
     */
    public SiteResource getSite()
    {
        return site;
    }
    
    /**
     * Returns the primary domain name mapping for the current site.
     * 
     * @return the primary domain name mapping for the current site, or null if none chosen.
     * @throws SiteException if the mapping could not be resoulved.
     */
    public String getSitePrimaryMapping() 
        throws SiteException
    {
        return siteService.getPrimaryMapping(getCoralSession(context), site);
    }

    /**
     * Returns the site configured as global components data source.
     */
    public SiteResource getGlobalComponentsDataSite()
    {
        return globalComponentsDataSite;
    }
    
    /**
     * Returns home page of currently viewed site.
     */
    public NavigationNodeResource getHomePage()
    {
        return homePage;
    }

    /**
     * Returns currently viewed navigation node.
     */
    public NavigationNodeResource getNode()
    {
        return node;
    }
    
    /**
     * Returns the navigation node providing content.
     * <p>
     * Generally {@code contentNode} is synonymous to {@code node}. When {@code doc_id} request
     * parameter is defined, and it's value is the identifier of a valid NavigationNodeResource that
     * the current user can view, {@node contentNode} is determined by {@code doc_id}.
     * </p>
     * 
     * @return navigation node providing content.
     */
    public NavigationNodeResource getContentNode()
    {
        return contentNode;
    }

    /**
     * Returns <code>true</code> if current navigation node is available,
     * it is also <code>true</code> if <code>site_id</code> parameter is defined.
     */
    public boolean isNodeDefined()
    {
        return node != null;
    }
    
    /** 
     * Returns current node's combined configuration.
     */
    public Parameters getPreferences()
    {
        return preferences;
    }
    
    /** 
     * Returns the system configuration.
     */
    public Parameters getSystemPreferences()
    {
        return systemPreferences;
    }    

    /**
     * Returns name of a browsing mode for current site.
     */
    public String getBrowseMode()
    {
        Map<String, String> modes = getBrowseModes(false);
        String mode = BROWSE_MODE_BROWSE;
        if(modes != null && site != null)
        {
            String siteName = site.getName();
            if(modes.containsKey(siteName))
            {
                mode = modes.get(siteName);
            }
        }
        if(adminMode)
        {
            mode = BROWSE_MODE_ADMINISTER;
        }
        if(modeOverride != null)
        {
            mode = modeOverride;
        }
        return mode;
    }
    
    /**
     * Sets a current browsing mode for current site.
     */
    public void setBrowseMode(String mode)
    {
        Map<String, String> modes = getBrowseModes(true);
        modes.put(site.getName(), mode);
    }
    
    /**
     * Overrides the browse mode for the current request.
     *
     * @param mode the mode.
     */
    public void setBrowseModeOverride(String mode)
    {
        modeOverride = mode;
    }
    
    /**
     * Overrides the administrative mode for the current request.
     *
     * @param mode the mode.
     */
    public void setAdminMode(boolean mode)
    {
        this.adminMode = mode;
    }

    /**
     * Set the date.
     *
     * @param date the date.
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * Returns the skin selected for the current site.
     */
    public String getSkinName()
    {
        return skinName;
    }
    
    /**
     * Checks if skin was overridden using request parameters
     */
    public boolean isSkinNameOverriden()
    {
        return skinNameOverridden;
    }

    /**
     * Override the skin for the duration of the current request.
     */
    public void setSkinName(String skin)
    {   
        skinName = skin;
    }
    
    public CmsComponentData nextComponent(String instanceName, String app, String clazz)
    throws ProcessingException
    {
        componentData = new CmsComponentData(this, instanceName, app, clazz);
        
        // Check if this instance name does not have any duplicates in current layout
        if(renderedComponentInstances == null)
        {
            renderedComponentInstances = new HashSet();
        }
        if(renderedComponentInstances.contains(instanceName))
        {
            componentData.error("Duplicate component instance name in layout", null);
        }
        renderedComponentInstances.add(instanceName);
        
        return componentData;
    }
    
    public CmsComponentData nextComponent(String instanceName)
    throws ProcessingException
    {
        return nextComponent(instanceName, null, null);
    }
    
    public CmsComponentData getComponent(String instanceName)
    throws ProcessingException
    {
        return new CmsComponentData(this, instanceName, null, null);
    }

    public CmsComponentData getComponent()
    {
        return componentData;
    }

    /**
     * Returns the configuration for the screen embedded in the current node.
     * 
     * @return screen configuration.
     */
    public Parameters getEmbeddedScreenConfig()
    {
        return getEmbeddedScreenConfig(this.node);
    }
    
    public Parameters getEmbeddedScreenConfig(NavigationNodeResource navigationNode)
    {
        Parameters nodeConfig = preferencesService.getCombinedNodePreferences(getCoralSession(context), navigationNode);
        String app = CmsComponentData.getParameter(nodeConfig,"screen.app",null);
        String screen = CmsComponentData.getParameter(nodeConfig,"screen.class",null);
        
        Parameters screenConfig = nodeConfig.getChild("screen.config."
            +app+"."+screen.replace(',','.')+".");
        Parameters config = new DefaultParameters();
        config.set("app", app);
        config.set("class", screen);
        config.add(screenConfig, true);
        return new DefaultParameters(config);        
    }

    Logger getLog()
    {
        return logger;
    }
    
    // implementation //////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns home page of a given site.
     */
    private NavigationNodeResource getHomePage(CoralSession coralSession, SiteResource site)
        throws ProcessingException
    {
        try
        {
            return structureService.getRootNode(coralSession, site);
        }
        catch(Exception e)
        {
            String msg = "Cannot get a home page node for site with id="+site.getIdString();
            logger.error(msg,e);
            throw new ProcessingException(msg, e);
        }
    }
    
    /**
     * Returns node with a given id.
     */
    private NavigationNodeResource getNode(long node_id)
        throws ProcessingException
    {
        try
        {
        	return StructureUtil.getNode(getCoralSession(context), node_id);
        }
        catch (ProcessingException e)
        {
            logger.error("cannot retrieve navigation node with id="+node_id, e);
            throw e;
        }
    }

    private Map<String, String> getBrowseModes(boolean create)
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        Map<String, String> modes = (Map<String, String>)(httpContext
            .getSessionAttribute(BROWSE_MODES_KEY));
        if(modes == null && create)
        {
            modes = new HashMap<String, String>();
            httpContext.setSessionAttribute(BROWSE_MODES_KEY, modes);
        }
        return modes;
    }
    
    private CoralSession getCoralSession(Context context)
    {
        return context.getAttribute(CoralSession.class);
    }

    public boolean checkAdministrator(CoralSession coralSession)
    {
        SiteResource site = getSite();
        Subject subject = coralSession.getUserSubject();
        if(site != null)
        {
            if(subject.hasRole(site.getAdministrator()))
            {
                return true;
            }
        }
        Role cmsAdministrator = coralSession.getSecurity().
            getUniqueRole("cms.administrator");
        return subject.hasRole(cmsAdministrator);
    }
    
    public boolean isApplicationEnabled(String appName) throws ProcessingException 
    {
        CoralSession coralSession = getCoralSession(context);
        ApplicationResource app = integrationService.getApplication(coralSession, appName);
        if(app == null) 
        {
            return true;
        }
        return integrationService.isApplicationEnabled(coralSession, getSite(), app);
    }
}
