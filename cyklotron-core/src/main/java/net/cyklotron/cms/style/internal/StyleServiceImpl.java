package net.cyklotron.cms.style.internal;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.labeo.services.BaseService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.AmbigousNameException;
import net.labeo.services.resource.CircularDependencyException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.query.MalformedQueryException;
import net.labeo.services.resource.query.QueryResults;
import net.labeo.services.templating.Context;
import net.labeo.services.templating.TemplatingService;

import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.ComponentSocketResourceImpl;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;
import net.cyklotron.cms.style.LevelResource;
import net.cyklotron.cms.style.LevelResourceImpl;
import net.cyklotron.cms.style.StyleException;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;
import net.cyklotron.cms.style.StyleService;

public class StyleServiceImpl
    extends BaseService
    implements StyleService
{
    /** the resource service. */
    protected ResourceService resourceService;
 
    /** logging facility */
    private LoggingFacility log;    
   
    // initialization ////////////////////////////////////////////////////////

    public void init()
    {
        resourceService = (ResourceService)broker.
            getService(ResourceService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(LOGGING_FACILITY);
    }

    // styles ////////////////////////////////////////////////////////////////
    
    /** 
     * add new style to system.
     *
     * @param name the style name.
     * @param description the style description.
     * @param site the site.
     * @param parent the parent style or <code>null</code> for top level style.
     * @param subject the creator.
     * @return style resource.
     */
    public StyleResource addStyle(String name, String description, 
                                  SiteResource site, StyleResource parent, 
                                  Subject subject)
        throws StyleException, AmbigousNameException
    {
        StyleResource style = null;
        if(getStyle(site, name) != null)
        {
            throw new AmbigousNameException("style "+name+" already exists for site "+site.getName());
        }

        try
        {
            Resource p = (parent != null) ? parent : getStyleRoot(site);
            style = StyleResourceImpl.createStyleResource(resourceService, name, p, subject);
            style.setDescription(description);
            style.update(subject);
        }
        catch(ValueRequiredException e)
        {
            throw new StyleException("Value required exception",e);
        }
        return style;
    }
    
    /**
     * returns the documents that have the style explicytly set.
     * 
     * @param style the style.
     */
    public List getReferringNodes(StyleResource style)
        throws StyleException
    {
        QueryResults results = null;
        try
        {
            results = resourceService.getQuery().
                executeQuery("FIND RESOURCE FROM structure.navigation_node WHERE style = "+
                             style.getIdString());
        }
        catch(MalformedQueryException e)
        {
            throw new StyleException("MalformedQueryException :",e);
        }
        Resource[] res = results.getArray(1);
        return Arrays.asList(res);
    }
    
    /** 
     * delete the style from the system.
     *
     * @param style the style to delete.
     * @param subject the subject performing delete action.
     */
    public void deleteStyle(StyleResource style, Subject subject)
        throws StyleException
    {
        if(getReferringNodes(style).size() > 0)
        {
            throw new StyleException("Style in use");
        } 
        LevelResource[] levels = getLevels(style);
        try
        {
            for(int i = 0; i<levels.length; i++)
            {
                deleteLevel(levels[i],subject);
            }
            resourceService.getStore().deleteResource(style);
        }
        catch(EntityInUseException e)
        {
            throw new StyleException("Entity in use exception",e);
        }
    }

    /**
     * Update the style info.
     *
     * @param style the style resource.
     * @param name the name of the style.
     * @param description the description of the style.
     * @param parent the parent style or <code>null</code> for top level style.
     * @param subject the subject who performs the action.
     */
    public void updateStyle(StyleResource style, String name, String description, 
                            StyleResource parent, Subject subject)
        throws CircularDependencyException, AmbigousNameException, StyleException
    {
        SiteResource site = getSite(style);
        if(!name.equals(style.getName()))
        {
            if(getStyle(site, name) != null)
            {
                throw new AmbigousNameException("style "+name+" already exists for site "+
                                                site.getName());
            }
            resourceService.getStore().setName(style, name);
        }
        Resource parentRes;
        if(parent == null)
        {
            parentRes = getStyleRoot(site);
        }
        else
        {
            parentRes = parent;
        }
        if(!style.getParent().equals(parentRes))
        {
            resourceService.getStore().setParent(style, parentRes);
        }
        String desc = style.getDescription();
        if(!description.equals(desc))
        {
            style.setDescription(description);
            style.update(subject);
        }
    }

    /**
     * Returns the site a style belongs to.
     *
     * @param syle the style resource.
     * @return the site.
     */
    public SiteResource getSite(StyleResource style)
        throws StyleException
    {
        return CmsTool.getSite(style);
    }

    /** 
     * Return the style resource for the given site and style name.
     *
     * @param site the site.
     * @param style the style name.
     * @return style resource, or <code>null</code> if not found.
     */
    public StyleResource getStyle(SiteResource site, String style)
        throws StyleException
    {
        Resource[] children = resourceService.getStore().
            getResource(getStyleRoot(site));
        Resource found = null;
        ArrayList stack = new ArrayList();
        for(int i=0; i<children.length; i++)
        {
            stack.add(children[i]);
        }
        while(stack.size() > 0)
        {
            Resource res = (Resource)stack.remove(stack.size()-1);
            if(res.getName().equals(style))
            {
                if(found != null)
                {
                    throw new StyleException("ambigous style name "+style+
                                              " for site "+site.getName());
                }
                else
                {
                    found = res;
                }
            }
        }
        return (StyleResource)found;
    }
    
    /**
     * Returns the super style of a style.
     *
     * @param style the style resource.
     * @returns the super style of the given style, or <code>null</code> for a
     *          top level style.
     */
    public StyleResource getSuperStyle(StyleResource style)
    {
        return (StyleResource)style.getParent();
    }
    
    /**
     * Returns the sub styles of a style.
     *
     * @param style the style resource.
     * @returns the sub styles of a style.
     */
    public StyleResource[] getSubStyles(StyleResource style)
    {
        Resource[] res = resourceService.getStore().getResource(style);
        StyleResource[] result = new StyleResource[res.length];
        for(int i=0; i<res.length; i++)
        {
            result[i] = (StyleResource)res[i];
        }
        return result;
    }

    /**
     * Returns the full list of styles.
     *
     * @param site the site.
     * @return the style resource.
     */
    public StyleResource[] getStyles(SiteResource site)
        throws StyleException
    {
        ArrayList list = new ArrayList();
        getStyles(getStyleRoot(site), list);
        StyleResource[] result = new StyleResource[list.size()];
        list.toArray(result);
        return result;
    }

    /**
     * Traverses style tree recursively.
     *
     * @param subject the subject.
     * @param resource the parent resource.
     * @param list the target list.
     */    
    private void getStyles(Resource resource, List list)
    {
        Resource[] children = resourceService.getStore().getResource(resource);
        for(int i = 0; i < children.length; i++)
        {
            if(children[i] instanceof StyleResource)
            {
                list.add(children[i]);
                getStyles(children[i], list);
            }
        }
    }

    /**
     * Returns the style root resource for a given site.
     *
     * @param site the site.
     * @return the style root resource for a given site.
     */
    public Resource getStyleRoot(SiteResource site)
        throws StyleException
    {
        Resource[] res = resourceService.
            getStore().getResource(site, "styles");
        if(res.length == 0)
        {
            throw new StyleException("styles root for site "+site.getName()+
                                                  " not found");
        }
        if(res.length > 1)
        {
            throw new StyleException("multiple style roots for site "+site.getName());
        }
        res = resourceService.
            getStore().getResource(res[0], "styles");
        if(res.length == 0)
        {
            throw new StyleException("styles/styles node for site "+site.getName()+
                                     " not found");
        }
        if(res.length > 1)
        {
            throw new StyleException("multiple style roots for site "+site.getName());
        }
        return res[0];
    }

    // levels ////////////////////////////////////////////////////////////////

    /**
     * Add level to the style.
     *
     * @param style the style.
     * @param layout the layout.
     * @param level the level.
     * @param description the description.
     * @param subject the creator.
     * @return the level resource.
     */
    public LevelResource addLevel(StyleResource style, LayoutResource layout, 
                                  int level, String description, Subject subject)
        throws StyleException
    {
        if(getLevel(style,level) != null)
        {
            throw new StyleException("level already definied for the style");
        }
        LevelResource levelResource = null;
        try
        {
            levelResource = LevelResourceImpl.createLevelResource(resourceService, 
                                                                  ""+level, style, subject);
            levelResource.setDescription(description);
            if(layout != null)
            {
                levelResource.setLayout(layout);
            }
            levelResource.update(subject);
        }
        catch(ValueRequiredException e)
        {
            throw new StyleException("Value required exception",e);
        }
        return levelResource;
    }

    /** 
     * delete the level from the system.
     *
     * @param level the level to delete.
     * @param subject the subject performing delete action.
     */
    public void deleteLevel(LevelResource level, Subject subject)
        throws StyleException
    {
        try
        {
            resourceService.getStore().deleteResource(level);
        }
        catch(EntityInUseException e)
        {
            throw new StyleException("Entity in use exception",e);
        }
    }

    /** 
     * Return the level for the style and level.
     *
     * @param style the style.
     * @param level the level.
     * @return the level resources.
     */    
    public LevelResource getLevel(StyleResource style, int level)
    {
        Resource[] resources = resourceService.getStore().getResource(style,""+level);
        if(resources == null || resources.length == 0)
        {
            return null;
        }
        for(int i = 0; i < resources.length; i++)
        {
            if(resources[i] instanceof LevelResource)
            {
                return (LevelResource)resources[i];
            }
        }
        return null;
    }
    
    /** 
     * Return all definied levels for the style.
     *
     * @param style the style.
     * @return the list of level resources.
     */
    public LevelResource[] getLevels(StyleResource style)
    {
        ArrayList list = new ArrayList();
        Resource[] resources = resourceService.getStore().getResource(style);
        for(int i = 0; i < resources.length; i++)
        {
            if(resources[i] instanceof LevelResource)
            {
                list.add(resources[i]);
            }
        }
        LevelResource[] levels = new LevelResource[list.size()];
        list.toArray(levels);
        return levels;
    }

    // layouts ///////////////////////////////////////////////////////////////

    /**
     * Return the layout appropriate to the given style and level.
     *
     * @param style the style resource.
     * @param level the level.
     * @return the layout resource.
     */
    public String getLayout(StyleResource style, int level)
    {
        log.debug("looking for style - "+style.getName()+" and level - "+level);
        LevelResource levelResource = getLevel(style, level);
        if(levelResource == null)
        {
            log.debug("level "+level+" not definied for style "+style.getName());
            if(level == 0)
            {
                if(style.getParent() instanceof StyleResource)
                {
                    // fallback to level 0 in parent style
                    return getLayout((StyleResource)style.getParent(),level);
                }
                else
                {
                    // nothing to fallback to
                    throw new IllegalStateException("undefined level 0 layout in "+
                                                    "top level style #"+style.getIdString()+
                                                    " ("+style.getName()+")");
                }
            }
            else
            {
                // fallback to lower level in the same style
                return getLayout(style, level-1);
            }
        }
        else
        {
            LayoutResource layout = levelResource.getLayout();
            if(layout == null)
            {
                log.debug("level definied but transparent");
                // fallback to parent style
                if(style.getParent() instanceof StyleResource)
                {
                    return getLayout((StyleResource)style.getParent(),level);
                }
                else
                {
                    // nothing to fallback to
                    throw new IllegalStateException("misplaced transparent level "+level+
                                                    " in top level style #"+style.getIdString()+
                                                    " ("+style.getName()+")");
                }
            }
            else
            {
                log.debug("found: "+ layout.getName());
                return layout.getName();
            }
        }
    }


    /** 
     * Add new layout to the system.
     *
     * @param name the name of the layout.
     * @param description the description of the layout.
     * @param target the layout template.
     * @param site the site.
     * @param subject the creator.
     * @return layout resource.
     */
    public LayoutResource addLayout(String name, String description, 
                                    SiteResource site, Subject subject)
        throws StyleException, AmbigousNameException
    {
        LayoutResource layout = null;
        if(getLayout(site, name) != null)
        {
            throw new AmbigousNameException("layout "+name+
                                            " already exists in site "+
                                            site.getName());
        }
        try
        {
            layout = LayoutResourceImpl.
                createLayoutResource(resourceService, name, getLayoutRoot(site), subject);
            layout.setDescription(description);
            layout.update(subject);
        }
        catch(ValueRequiredException e)
        {
            throw new StyleException("Value required exception",e);
        }
        return layout;
    }
    
    /** 
     * delete the layout from the system.
     *
     * @param layout the layout to delete.
     * @param subject the subject performing delete action.
     */
    public void deleteLayout(LayoutResource layout, Subject subject)
        throws StyleException
    {
        try
        {
            resourceService.getStore().deleteResource(layout);
        }
        catch(EntityInUseException e)
        {
            throw new StyleException("Entity in use exception",e);
        }
    }

    /**
     * Update the layout info.
     *
     * @param layout the layout resource.
     * @param name the name of the layout.
     * @param description the description of the layout.
     * @param target the target that the layout points to.
     * @param subject the subject who performs the action.
     */
    public void updateLayout(LayoutResource layout, String name, 
                             String description, Subject subject)
        throws StyleException, AmbigousNameException
    {
        SiteResource site = getSite(layout);
        if(!name.equals(layout.getName()))
        {
            if(getLayout(site, name) != null)
            {
                throw new AmbigousNameException("layout "+name+
                                                " already exists in site "+
                                                site.getName());
            }
            resourceService.getStore().setName(layout, name);
        }
        boolean update = false;
        if(!description.equals(layout.getDescription()))
        {
            layout.setDescription(description);
            update = true;
        }
        if(update)
        {
            layout.update(subject);
        }
    }

    /**
     * Returns the site a layout belongs to.
     *
     * @param layout the layout.
     * @return the site.
     */
    public SiteResource getSite(LayoutResource layout)
        throws StyleException
    {
        return CmsTool.getSite(layout);
    }

    /**
     * Return the layout.
     *
     * @param site the site.
     * @param layout the name of the layout.
     * @return the layout resource, or <code>null</code> if not found.
     */
    public LayoutResource getLayout(SiteResource site, String layout)
        throws StyleException
    {
        Resource[] res = resourceService.getStore().getResource(getLayoutRoot(site), layout);
        if(res.length == 0)
        {
            return null;
        }
        if(res.length > 1)
        {
            throw new StyleException("ambigous layout name "+layout+" for site "+
                                      site.getName());
        }
        return (LayoutResource)res[0];
    }

    /**
     * Return full list of layouts.
     *
     * @param site the site.
     * @return the list of layouts.
     */
    public LayoutResource[] getLayouts(SiteResource site)
        throws StyleException
    {
        Resource[] result = resourceService.getStore().getResource(getLayoutRoot(site));
        LayoutResource[] layouts = new LayoutResource[result.length];
        for(int i = 0; i < result.length; i++)
        {
            layouts[i]=(LayoutResource)result[i];
        }
        return layouts;
    }

    /**
     * Returns the layout root resource for a given site.
     *
     * @param site the site.
     * @return the layout root resource for a given site.
     */
    public Resource getLayoutRoot(SiteResource site)
        throws StyleException
    {
        Resource[] res = resourceService.getStore().getResource(site, "styles");
        if(res.length == 0)
        {
            throw new StyleException("styles root for site "+site.getName()+
                                     " not found");
        }
        if(res.length > 1)
        {
            throw new StyleException("multiple layout roots for site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], "layouts");
        if(res.length == 0)
        {
            throw new StyleException("styles/layouts node for site "
                                     +site.getName()+
                                     " not found");
        }
        if(res.length > 1)
        {
            throw new StyleException("multiple layout roots for site "+site.getName());
        }
        return res[0];
    }

    /**
     * Return the component sockets in a layout.
     *
     * @param layout the layout.
     */
    public ComponentSocketResource[] getSockets(LayoutResource layout)
        throws StyleException
    {
        Resource[] res = resourceService.getStore().getResource(layout);
        ArrayList temp = new ArrayList();
        for(int i=0; i<res.length; i++)
        {
            if(res[i] instanceof ComponentSocketResource)
            {
                temp.add(res[i]);
            }
        }
        ComponentSocketResource[] result = new ComponentSocketResource[temp.size()];
        temp.toArray(result);
        return result;
    }
    
    /**
     * Adds a component socket to a layout.
     *
     * @param layout the layout
     * @param name the name of the socket.
     * @param subject the subject that performs the operation.
     */
    public ComponentSocketResource addSocket(LayoutResource layout, 
                                             String name, Subject subject)
        throws StyleException
    {
        try
        {
            return ComponentSocketResourceImpl.
                createComponentSocketResource(resourceService, name, layout, subject);
        }
        catch(Exception e)
        {
            throw new StyleException("socket creation failed", e);
        }
    }
    
    /**
     * Deletes a component socket from a layout.
     *
     * @param layout the layout.
     * @param name the name of the socket.
     * @param subject the subject that performs the operation.
     */
    public void deleteSocket(LayoutResource layout, String name, Subject subject)
        throws StyleException
    {
        try
        {
            Resource[] res = resourceService.getStore().getResource(layout, name);
            ComponentSocketResource socket = (ComponentSocketResource)res[0];
            resourceService.getStore().deleteResource(socket);
        }
        catch(Exception e)
        {
            throw new StyleException("socket removal failed", e);
        }   
    }

    /**
     * Parses the velocity template and finds declared component sockets.
     *
     * @param templateContents.
     * @return names of the declared sockets.
     */
    public String[] findSockets(String templateContents)
        throws StyleException
    {
        TemplatingService templatingService = (TemplatingService)
            broker.getService(TemplatingService.SERVICE_NAME);
        Reader r = new StringReader(templateContents);
        Writer w = new StringWriter();
        Context context = templatingService.createContext();
        List sockets = new ArrayList();
        context.put("sockets", sockets);
        context.put("component", new FakeComponentTool(context));
        try
        {
            templatingService.merge("cms", context, r, w, "<uploaded file>");
        }
        catch(Exception e)
        {
            throw new StyleException("failed to parse template", e);
        }
        String[] socketNames = new String[sockets.size()];
        sockets.toArray(socketNames);
        return socketNames;
    }

    public static class FakeComponentTool
    {
        private Context context;
        
        public FakeComponentTool(Context context)
        {
            this.context = context;
        }

        public void include(String app, String component, List params)
        {
            if(app.equals("cms") && component.equals("CMSComponentWrapper"))
            {
                for(int i=0; i<params.size(); i++)
                {
                    if(params.get(i) instanceof List)
                    {
                        List p = (List)params.get(i);
                        if(p.get(0) instanceof String && p.get(0).equals("instance"))
                        {
                            List sockets = (List)context.get("sockets");
                            sockets.add(p.get(1));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Matches a set of sockets in a template with sockets defined for a layout.
     * 
     * @param layout a layout object.
     * @param sockets list of socket names.
     * @return <code>true</code> if the sockets sets are identical.
     */
    public boolean matchSockets(LayoutResource layout, String[] templateSockets)
        throws StyleException
    {
        Set s1 = new HashSet();
        for (int i = 0; i < templateSockets.length; i++)
        {
            s1.add(templateSockets[i]);
        }
        ComponentSocketResource[] layoutSockets = getSockets(layout); 
        Set s2 = new HashSet();
        for (int i = 0; i < layoutSockets.length; i++)
        {
            s2.add(layoutSockets[i].getName());
        }
        return s1.equals(s2);
    }
}

