package net.cyklotron.cms;

import java.util.Date;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

public class CmsData
    implements CmsConstants
{
    // static api
    public static CmsData getCmsData(Object data)
        throws ProcessingException
    {
        return null;
    }
    
    public static void removeCmsData(Object data)
        throws ProcessingException
    {
    }
    
    // public interface    // ///////////////////////////////////////////////////////

    /**
     * Returns current CMS time.
     */
    public Date getDate()
    {
        return null;
    }

    /**
     * Return the user data.
     *
     * @return the subject.
     */
    public Object getUserData()
    {
        return null;
    }    

    /**
     * Returns currently browsed, edited site.
     */
    public SiteResource getSite()
    {
        return null;
    }

    /**
     * Returns the site configured as global components data source.
     */
    public SiteResource getGlobalComponentsDataSite()
    {
        return null;
    }
    
    /**
     * Returns home page of currently viewed site.
     */
    public NavigationNodeResource getHomePage()
    {
        return null;
    }

    /**
     * Returns currently viewed navigation node.
     */
    public NavigationNodeResource getNode()
    {
        return null;
    }

    public boolean isNodeDefined()
    {
        return false;
    }
    
    /** 
     * Returns current node's combined configuration.
     */
    public Parameters getPreferences()
    {
        return null;
    }
    
    /** 
     * Returns the system configuration.
     */
    public Parameters getSystemPreferences()
    {
        return null;
    }    

    /**
     * Returns name of a browsing mode for current site.
     */
    public String getBrowseMode()
    {
        return null;
    }
    
    /**
     * Sets a current browsing mode for current site.
     */
    public void setBrowseMode(String mode)
    {
    }
    
    /**
     * Overrides the browse mode for the current request.
     *
     * @param mode the mode.
     */
    public void setBrowseModeOverride(String mode)
    {
    }

    /**
     * Set the date.
     *
     * @param date the date.
     */
    public void setDate(Date date)
    {
    }

    /**
     * Returns the skin selected for the current site.
     */
    public String getSkinName()
    {
        return null;
    }
    
    /**
     * Override the skin for the duration of the current request.
     */
    public void setSkinName(String skin)
    {   
    }
    
    public Object nextComponent(String instanceName, String app, String clazz)
        throws ProcessingException
    {
        return null;
    }
    
    public Object nextComponent(String instanceName)
        throws ProcessingException
    {
        return null;
    }

    public Object getComponent()
    {
        return null;
    }
}
