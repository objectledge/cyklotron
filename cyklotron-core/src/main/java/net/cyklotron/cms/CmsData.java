package net.cyklotron.cms;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.StructureUtil;

/**
 * A data object used to encapsulate CMS runtime data.
 *
 * @author <a href="mailto:zwierzem@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsData.java,v 1.3 2005-01-18 17:38:09 pablo Exp $
 */
public class CmsData
    implements CmsConstants
{
    // static api
    
    public static CmsData getCmsData(RunData data)
    throws ProcessingException
    {
        CmsData cmsData = (CmsData)(data.getContext().get(CMS_DATA_KEY));
        if(cmsData == null)
        {
            cmsData = new CmsData(data);
            data.getContext().put(CMS_DATA_KEY, cmsData);
            if(cmsData.getNode() != null) // TODO: Remove this block after CmsData is widely used
            {
                // store values in the context
                data.getContext().put("node", cmsData.getNode());
                data.getContext().put("home_page_node", cmsData.getHomePage());
                data.getContext().put("site", cmsData.getSite());
            }
        }
        return cmsData;
    }
    
	public static void removeCmsData(RunData data)
	throws ProcessingException
	{
		data.getContext().remove(CMS_DATA_KEY);
	}

    // services and utility objects
    /** The {@link Logger} */
    private Logger log;
    /** resource service */
    private CoralSession resourceService;
    /** structure service */
    private StructureService structureService;
    /** preferences service */
    private PreferencesService preferencesService;
    /** site service */
    private SiteService siteService;
    
    // attributes
    private RunData data;
    
    private boolean adminMode;
    private String modeOverride;
    private SiteResource site;
    private NavigationNodeResource node;
    private NavigationNodeResource homePage;
    private Configuration preferences;
    private Configuration systemPreferences;
    private SiteResource globalComponentsDataSite;
    private String skinName;
    
    private Date date;

    private Set renderedComponentInstances;
    private CmsComponentData componentData;

    private UserData userData;
    
    // initialization ////////////////////////////////////////////////////////
    
    private CmsData(RunData data)
    throws ProcessingException
    {
        this.data = data;
        // init cms data
        init(data.getBroker());
        nodesSetup(data);
        preferencesSetup(data);            
        // get date from session
        date = (Date)(data.getGlobalContext().getAttribute(CMS_DATE_KEY));
        if(date == null)
        {
            date = new Date();
        }
    }

    private void init(ServiceBroker broker)
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("cmsdata");
        resourceService = (CoralSession)broker.getService(CoralSession.SERVICE_NAME);
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
        preferencesService = (PreferencesService)(broker.getService(PreferencesService.SERVICE_NAME));
        siteService = (SiteService)(broker.getService(SiteService.SERVICE_NAME));
    }
    
    private void nodesSetup(RunData data)
        throws ProcessingException
    {
        adminMode = true;
        modeOverride = null;
        if(data.getParameters().get("x").isDefined())
        {
            node = getNode(data.getParameters().get("x").asLong());
            adminMode = false;
        }

        // 1. Get the node
        if(node == null) // node not found using x parameter
        {
            // We are in admin mode - possible parameters are site_id and/or node_id or none
            long node_id = data.getParameters().get("node_id").asLong(-1);
            if(node_id != -1)
            {
                node = getNode(node_id);
            }
            else
            {
                long site_id = data.getParameters().get("site_id").asLong(-1);
                if(site_id != -1)
                {
                    try
                    {
                        Resource siteRes = resourceService.getStore().getResource(site_id);
                        if(siteRes instanceof SiteResource)
                        {
                            site = (SiteResource)siteRes;
                        }
                        else
                        {
                            log.error("Resource with a given id="+site_id+" is not a site");
                        }
                    }
                    catch (EntityDoesNotExistException e)
                    {
                        log.error("Site with id="+site_id+" does not exist", e);
                    }
                    node = getHomePage(site);
                }
            }
        }

        if(node != null)
        {
            site = node.getSite();
            homePage = getHomePage(site);
        }
    }

    protected void preferencesSetup(RunData data)
        throws ProcessingException
    {
        systemPreferences = preferencesService.getSystemPreferences(); 
        if(node != null)
        {
            preferences = preferencesService.getCombinedNodePreferences(node);
        }
        else
        {
            preferences = systemPreferences;
        }
        String globalComponentsDataSiteName = systemPreferences.get("globalComponentsData").asString(null);
        if(globalComponentsDataSiteName != null)
        {
            try
            {
                globalComponentsDataSite = siteService.getSite(globalComponentsDataSiteName); 
            }
            catch(SiteException e)
            {
                throw new ProcessingException("globalComponentsData is set to noexistent site "+globalComponentsDataSiteName);
            }
        }
        skinName = preferences.get("site.skin").asString("default");
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
        Subject subject = null;
        if(userData == null)
        {
            Principal principal = data.getUserPrincipal();
            try
            {
                if (principal != null)
                {
                    String username = principal.getName();
                    subject = resourceService.getSecurity().getSubject(username);
                }
            }
            catch(EntityDoesNotExistException e)
            {
                log.debug("CmsData ",e);
                subject = null;
            }
            userData = new UserData(data, subject);
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
     * Returns <code>true</code> if current navigation node is available,
     * it is also <code>true</code> if <code>site_id</code> parameter is defined.
     * TODO: Should be called isInSite() or something
     */
    public boolean isNodeDefined()
    {
        return node != null;
    }
    
    /** 
     * Returns current node's combined configuration.
     */
    public Configuration getPreferences()
    {
        return preferences;
    }
    
    /** 
     * Returns the system configuration.
     */
    public Configuration getSystemPreferences()
    {
        return systemPreferences;
    }    

    /**
     * Returns name of a browsing mode for current site.
     */
    public String getBrowseMode()
    {
        Map modes = getBrowseModes();
        String mode = BROWSE_MODE_ADMINISTER;
        if(site != null)
        {
            String siteName = site.getName();
            mode = (String)(modes.get(siteName));
            if(mode == null)
            {
                mode = BROWSE_MODE_BROWSE;
                modes.put(siteName, mode);
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
        Map modes = getBrowseModes();
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

    public CmsComponentData getComponent()
    {
        return componentData;
    }

    Logger getLog()
    {
        return log;
    }
    
    // implementation //////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns home page of a given site. TODO: Move to CmsUtil (??)
     */
    private NavigationNodeResource getHomePage(SiteResource site)
        throws ProcessingException
    {
        try
        {
            return structureService.getRootNode(site);
        }
        catch(Exception e)
        {
            String msg = "Cannot get a home page node for site with id="+site.getIdString();
            log.error(msg,e);
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
        	return StructureUtil.getNode(resourceService, node_id);
        }
        catch (ProcessingException e)
        {
            log.error("cannot retrieve navigation node with id="+node_id, e);
            throw e;
        }
    }

    private Map getBrowseModes()
    {
        Map modes = (Map)(data.getGlobalContext().getAttribute(BROWSE_MODES_KEY));
        if(modes == null)
        {
            modes = new HashMap();
            data.getGlobalContext().setAttribute(BROWSE_MODES_KEY, modes);
        }
        return modes;
    }
}
