package net.cyklotron.cms.integration.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.schema.ResourceClassInheritance;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.ComponentStateResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.integration.ScreenStateResource;

/**
 * @author <a href="mailto:rkrzewsk@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: IntegrationServiceImpl.java,v 1.3 2005-02-09 22:21:58 rafal Exp $
 */
public class IntegrationServiceImpl
    implements IntegrationService
{
    // instance variables ////////////////////////////////////////////////////

    /** logging facility */
    private Logger log;

    /** the application data root node. */
    protected Resource integrationRoot;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public IntegrationServiceImpl(Logger logger, CoralSessionFactory sessionFactory)
    {
        log = logger;
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            Resource res[] = coralSession.getStore().
                getResourceByPath("/cms/applications");
            if(res.length == 1)
            {   
                integrationRoot = res[0];
            }
            else
            {
                throw new ComponentInitializationError("failed to lookup /cms/applications node");
            }
        }
        finally
        {
            coralSession.close();
        }
    }

    // public interface //////////////////////////////////////////////////////
    
    /**
     * Returns the descriptors of all applications deployed in the system.
     */
    public ApplicationResource[] getApplications(CoralSession coralSession)
    {
        Resource[] res = coralSession.getStore().
            getResource(integrationRoot);
        ApplicationResource[] apps = new ApplicationResource[res.length];
        for(int i=0; i<res.length; i++)
        {
            apps[i] = (ApplicationResource)res[i];
        }
        return apps;
    }

    /**
     * Return the ApplicationResource for app
     * 
     * @param name the application name.
     * @return the application resource.
     */
    public ApplicationResource getApplication(CoralSession coralSession, String name)
    {
        Resource[] res = coralSession.getStore().getResource(integrationRoot, name);
        if(res.length != 1)
        {
            return null;
        }
        return (ApplicationResource)res[0];
    }
    
    /**
     * Returns the descriptors of all components provided by an application.
     *
     * @param app the application.
     */
    public ComponentResource[] getComponents(CoralSession coralSession, ApplicationResource app)
    {
        Resource[] res = coralSession.getStore().
            getResource(app, "components");
        if(res.length == 1)
        {
            res = coralSession.getStore().getResource(res[0]);
        }
        else
        {
            return new ComponentResource[0];
        }
        ComponentResource[] comps = new ComponentResource[res.length];
        for(int i=0; i<res.length; i++)
        {
            comps[i] = (ComponentResource)res[i];
        }
        return comps;        
    }

    /**
     * Returns a named component from a specific application.
     * 
     * @param app the application.
     * @param name the component name. 
     * @return the component resource.
     */
    public ComponentResource getComponent(CoralSession coralSession, ApplicationResource app, String name)
    {
        Resource[] res = coralSession.getStore().
            getResource(app, "components");
        if(res.length == 1)
        {
            res = coralSession.getStore().getResource(res[0], name);
            if(res.length == 1)
            {
                return (ComponentResource)res[0];
            }
        }
        return null;
    }
    
    /**
     * Returns the descirptors of all components deployed in the system.
     */
    public ComponentResource[] getComponents(CoralSession coralSession)
    {
        ApplicationResource[] apps = getApplications(coralSession);
        ArrayList comps = new ArrayList();
        for(int i=0; i<apps.length; i++)
        {
            if(apps[i].getEnabled())
            {
                comps.addAll(Arrays.asList(getComponents(coralSession, apps[i])));
            }
        }
        ComponentResource[] result = new ComponentResource[comps.size()];
        comps.toArray(result);
        return result;
    }
    
    /**
     * Returns the application a component belongs to.
     *
     * @param comp the component.
     */
    public ApplicationResource getApplication(CoralSession coralSession, ComponentResource comp)
    {
        Resource p = comp.getParent();
        while(p != null && !(p instanceof ApplicationResource))
        {
            p = p.getParent();
        }
        return (ApplicationResource)p;
    }

    /**
     * Returns the component with the given app and component name.
     *
     * @param app the Labeo application parameter.
     * @param name the Labeo component name.
     * @return the component, or <code>null</code> if not found.
     */
    public ComponentResource getComponent(CoralSession coralSession, String app, String name)
    {
        ComponentResource[] components = getComponents(coralSession);
        for(int i=0; i<components.length; i++)
        {
            if(getApplication(coralSession, components[i]).getApplicationName().equals(app) &&
               components[i].getComponentName().equals(name))
            {
                return components[i];
            }
        }
        return null;
    }

    /**
     * Get defined states of a component.
     *
     * @param component the component.
     * @return an array of defined states, or empty array if component is
     *         stateless. 
     */
    public ComponentStateResource[] getComponentStates(CoralSession coralSession, ComponentResource component)
    {
        Resource[] res = coralSession.getStore().getResource(component);
        ArrayList temp = new ArrayList();
        for(int i=0; i<res.length; i++)
        {
            if(res[i] instanceof ComponentStateResource)
            {
                temp.add(res[i]);
            }
        }
        ComponentStateResource[] result = new ComponentStateResource[temp.size()];
        temp.toArray(result);
        return result;
    }

    /**
     * Checks if a component has a given state defined.
     *
     * @param component the component.
     * @param state the state.
     */
    public boolean hasState(CoralSession coralSession, ComponentResource component, String state)
    {
        Resource[] res = coralSession.getStore().getResource(component, state);
        return (res.length == 1 && res[0] instanceof ComponentStateResource);
    }

    /**
     * Returns the descriptors of all screens provided by an application.
     *
     * @param app the application.
     */
    public ScreenResource[] getScreens(CoralSession coralSession, ApplicationResource app)
    {
        Resource[] res = coralSession.getStore().
            getResource(app, "screens");
        if(res.length == 1)
        {
            res = coralSession.getStore().getResource(res[0]);
        }
        else
        {
            return new ScreenResource[0];
        }
        ScreenResource[] comps = new ScreenResource[res.length];
        for(int i=0; i<res.length; i++)
        {
            comps[i] = (ScreenResource)res[i];
        }
        return comps;        
    }

    /**
     * Returns a named component from a specific application.
     * 
     * @param app the application.
     * @param name the component name. 
     * @return the component resource.
     */
    public ScreenResource getScreen(CoralSession coralSession, ApplicationResource app, String name)
    {
        Resource[] res = coralSession.getStore().
            getResource(app, "screens");
        if(res.length == 1)
        {
            res = coralSession.getStore().getResource(res[0], name);
            if(res.length == 1)
            {
                return (ScreenResource)res[0];
            }
        }
        return null;
    }
    
    /**
     * Returns the descirptors of all screens deployed in the system.
     */
    public ScreenResource[] getScreens(CoralSession coralSession)
    {
        ApplicationResource[] apps = getApplications(coralSession);
        ArrayList comps = new ArrayList();
        for(int i=0; i<apps.length; i++)
        {
            if(apps[i].getEnabled())
            {
                comps.addAll(Arrays.asList(getScreens(coralSession, apps[i])));
            }
        }
        ScreenResource[] result = new ScreenResource[comps.size()];
        comps.toArray(result);
        return result;
    }
    
    /**
     * Returns the application a screen belongs to.
     *
     * @param comp the screen.
     */
    public ApplicationResource getApplication(CoralSession coralSession, ScreenResource comp)
    {
        Resource p = comp.getParent();
        while(p != null && !(p instanceof ApplicationResource))
        {
            p = p.getParent();
        }
        return (ApplicationResource)p;
    }

    /**
     * Returns the screen with the given app and screen name.
     *
     * @param app the Labeo application parameter.
     * @param name the Labeo screen name.
     * @return the screen, or <code>null</code> if not found.
     */
    public ScreenResource getScreen(CoralSession coralSession, String app, String name)
    {
        ScreenResource[] screens = getScreens(coralSession);
        for(int i=0; i<screens.length; i++)
        {
            if(getApplication(coralSession, screens[i]).getApplicationName().equals(app) &&
               screens[i].getScreenName().equals(name))
            {
                return screens[i];
            }
        }
        return null;
    }

    /**
     * Get defined states of a screen.
     *
     * @param screen the screen.
     * @return an array of defined states, or empty array if screen is
     *         stateless. 
     */
    public ScreenStateResource[] getScreenStates(CoralSession coralSession, ScreenResource screen)
    {
        Resource[] res = coralSession.getStore().getResource(screen);
        ArrayList temp = new ArrayList();
        for(int i=0; i<res.length; i++)
        {
            if(res[i] instanceof ScreenStateResource)
            {
                temp.add(res[i]);
            }
        }
        ScreenStateResource[] result = new ScreenStateResource[temp.size()];
        temp.toArray(result);
        return result;
    }

    /**
     * Checks if a screen has a given state defined.
     *
     * @param screen the screen.
     * @param state the state.
     */
    public boolean hasState(CoralSession coralSession, ScreenResource screen, String state)
    {
        Resource[] res = coralSession.getStore().getResource(screen, state);
        return (res.length == 1 && res[0] instanceof ScreenStateResource);
    }

    public ResourceClassResource getResourceClass(CoralSession coralSession, String name)
    {
        ResourceClassResource[] classes = getResourceClasses(coralSession);
        for(int i=0; i<classes.length; i++)
        {
            if(classes[i].getName().equals(name))
            {
                return classes[i];
            }
        }
        return null;
    }

    /**
     * Returns the descirptors of all resource classes registered in the system.
     */
    public ResourceClassResource[] getResourceClasses(CoralSession coralSession)
    {
        ApplicationResource[] apps = getApplications(coralSession);
        ArrayList resClasses = new ArrayList();
        for(int i=0; i<apps.length; i++)
        {
            if(apps[i].getEnabled())
            {
                resClasses.addAll(Arrays.asList(getResourceClasses(coralSession, apps[i])));
            }
        }
        ResourceClassResource[] result = new ResourceClassResource[resClasses.size()];
        resClasses.toArray(result);
        return result;
    }
    
    /**
     * Returns the resource class info with the given app and resource class name.
     *
     * @param rc the resource class.
     * @return the resource class, or <code>null</code> if not found.
     */
    public ResourceClassResource getResourceClass(CoralSession coralSession, ResourceClass rc)
    {
        ApplicationResource[] apps = getApplications(coralSession);
        for(int i=0; i<apps.length; i++)
        {
            ResourceClassResource resourceClass = getResourceClass(coralSession, apps[i],rc);
            if(resourceClass != null)
            {
                return resourceClass;
            }
        }
        return null;
    }

    /**
     * Returns the descirptors of all resource classes provided by an application.
     *
     * @param applicationResource the application resource.
     */
    public ResourceClassResource[] getResourceClasses(CoralSession coralSession, ApplicationResource applicationResource)
    {
        Resource[] res = coralSession.getStore().getResource(applicationResource, "resources");
        if(res.length == 0)
        {
            return new ResourceClassResource[0];
        }
        res = coralSession.getStore().getResource(res[0]);
        ResourceClassResource[] rcs  = new ResourceClassResource[res.length];
        for(int i = 0; i < res.length; i++)
        {
            rcs[i] = (ResourceClassResource)res[i];
        }
        return rcs;
    }
    
    /**
     * Returns the resource class info with the given app and resource class name.
     *
     * @param app the application resource.
     * @param rc the resource class.
     * @return the resource class, or <code>null</code> if not found.
     */
    private ResourceClassResource getResourceClass(CoralSession coralSession, ApplicationResource app, ResourceClass rc)
    {
        Resource[] res = coralSession.getStore().getResource(app, "resources");
        if(res.length != 1)
        {
            return null;
        }
        
        res = coralSession.getStore().getResource(res[0], rc.getName());
        if(res.length == 1)
        {
            return (ResourceClassResource)res[0];
        }
        ResourceClassInheritance[] inheritance = rc.getInheritance();
        ResourceClassResource found = null;
        for(int i = 0; i < inheritance.length; i++)
        {
            if(inheritance[i].getChild().equals(rc))
            {
                ResourceClassResource rcr = getResourceClass(coralSession, app, inheritance[i].getParent());
                if(rcr != null)
                {
                    if(found != null)
                    {
                        throw new IllegalStateException(
                            "according to integration entries "
                                + rc.getName()
                                + " is both "
                                + rcr.getName()
                                + " and "
                                + found.getName()
                                + " you need to create explicit entry for "
                                + rc.getName() 
                                + " to fix it.");
                    }
                    else
                    {
                        found = rcr;
                    }
                }
            }
        }
        return found;
    }
    
    /**
     * Returns the resource class fot the given resource class resource.
     *
     * @param rcr the resource class resource.
     * @return the resource class for this resource class resource or <code>null</code>..
     */
    public ResourceClass getResourceClass(CoralSession coralSession, ResourceClassResource rcr)
    {
        try
        {
            return coralSession.getSchema().getResourceClass(rcr.getName());
        }
        catch(EntityDoesNotExistException e)
        {
            return null;
        }
    }

    public Map initResourceClassSelection(CoralSession coralSession, String items, String state)
    {
        if(items == null || items.length() == 0)
        {
            return new HashMap();
        }
        StringTokenizer st = new StringTokenizer(items, " ");
        Map map = new HashMap();
        while(st.hasMoreTokens())
        {
            ResourceClassResource res = getResourceClass(coralSession, st.nextToken());
            if(res != null)
            {
                map.put(res, state);
            }
        }
        return map;
    }

    /** 
     * Return the schema role root, null if schema root does not exist.
     *
     * @param rc the resource class.
     * @return the schema role root.
     */
    public Resource getSchemaRoleRoot(CoralSession coralSession, ResourceClass rc)
    {
        ResourceClassResource rcr = getResourceClass(coralSession, rc);
        if(rcr == null)
        {
            return null;
        }
        Resource[] resources = coralSession.getStore().getResource(rcr,"roles");
        if(resources.length == 0)
        {
            ResourceClass resourceClass = null;
            try
            {
                resourceClass = coralSession.getSchema().getResourceClass(rcr.getName());
                ResourceClass[] parents = resourceClass.getParentClasses();
                for(int i = 0; i < parents.length; i++)
                {
                    Resource schema = getSchemaRoleRoot(coralSession, parents[i]);
                    if(schema != null)
                    {
                        return schema;
                    }
                }
            }
            catch(EntityDoesNotExistException e)
            {
                log.error("It should never happen, see sources for more details",e);
            }
            return null;
        }
        return resources[0];
    }
}
