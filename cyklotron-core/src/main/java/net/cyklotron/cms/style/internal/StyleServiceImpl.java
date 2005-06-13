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

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.schema.CircularDependencyException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;

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
    implements StyleService
{
    /** logging facility */
    private Logger log;    

    private Templating templating;
    // initialization ////////////////////////////////////////////////////////

    public StyleServiceImpl(Logger logger, Templating templating)
    {
        log = logger;
        this.templating = templating;
    }

    // styles ////////////////////////////////////////////////////////////////
    
    /** 
     * add new style to system.
     *
     * @param name the style name.
     * @param description the style description.
     * @param site the site.
     * @param parent the parent style or <code>null</code> for top level style.
     * @return style resource.
     */
    public StyleResource addStyle(CoralSession coralSession, String name, String description, 
                                  SiteResource site, StyleResource parent)
        throws StyleException, AmbigousEntityNameException, InvalidResourceNameException
    {
        StyleResource style = null;
        if(getStyle(coralSession, site, name) != null)
        {
            throw new AmbigousEntityNameException("style "+name+" already exists for site "+site.getName());
        }
        Resource p = (parent != null) ? parent : getStyleRoot(coralSession, site);
        style = StyleResourceImpl.createStyleResource(coralSession, name, p);
        style.setDescription(description);
        style.update();
        return style;
    }
    
    /**
     * returns the documents that have the style explicytly set.
     * 
     * @param style the style.
     */
    public List getReferringNodes(CoralSession coralSession, StyleResource style)
        throws StyleException
    {
        QueryResults results = null;
        try
        {
            results = coralSession.getQuery().
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
     */
    public void deleteStyle(CoralSession coralSession, StyleResource style)
        throws StyleException
    {
        if(getReferringNodes(coralSession, style).size() > 0)
        {
            throw new StyleException("Style in use");
        } 
        LevelResource[] levels = getLevels(coralSession, style);
        try
        {
            for(int i = 0; i<levels.length; i++)
            {
                deleteLevel(coralSession, levels[i]);
            }
            coralSession.getStore().deleteResource(style);
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
     */
    public void updateStyle(CoralSession coralSession, StyleResource style, String name,
        String description, StyleResource parent)
        throws CircularDependencyException, AmbigousEntityNameException, StyleException,
        InvalidResourceNameException
    {
        SiteResource site = getSite(style);
        if(!name.equals(style.getName()))
        {
            if(getStyle(coralSession, site, name) != null)
            {
                throw new AmbigousEntityNameException("style "+name+" already exists for site "+
                                                site.getName());
            }
            coralSession.getStore().setName(style, name);
        }
        Resource parentRes;
        if(parent == null)
        {
            parentRes = getStyleRoot(coralSession, site);
        }
        else
        {
            parentRes = parent;
        }
        if(!style.getParent().equals(parentRes))
        {
            coralSession.getStore().setParent(style, parentRes);
        }
        String desc = style.getDescription();
        if(!description.equals(desc))
        {
            style.setDescription(description);
            style.update();
        }
    }

    /**
     * Returns the site a style belongs to.
     *
     * @param style the style resource.
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
    public StyleResource getStyle(CoralSession coralSession, SiteResource site, String style)
        throws StyleException
    {
        Resource[] children = coralSession.getStore().
            getResource(getStyleRoot(coralSession, site));
        Resource found = null;
        ArrayList<Resource> stack = new ArrayList<Resource>();
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
    public StyleResource[] getSubStyles(CoralSession coralSession, StyleResource style)
    {
        Resource[] res = coralSession.getStore().getResource(style);
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
    public StyleResource[] getStyles(CoralSession coralSession, SiteResource site)
        throws StyleException
    {
        ArrayList<StyleResource> list = new ArrayList<StyleResource>();
        getStyles(coralSession, getStyleRoot(coralSession, site), list);
        StyleResource[] result = new StyleResource[list.size()];
        list.toArray(result);
        return result;
    }

    /**
     * Traverses style tree recursively.
     *
     * @param resource the parent resource.
     * @param list the target list.
     */    
    private void getStyles(CoralSession coralSession, Resource resource, List<StyleResource> list)
    {
        Resource[] children = coralSession.getStore().getResource(resource);
        for(int i = 0; i < children.length; i++)
        {
            if(children[i] instanceof StyleResource)
            {
                list.add((StyleResource)children[i]);
                getStyles(coralSession, children[i], list);
            }
        }
    }

    /**
     * Returns the style root resource for a given site.
     *
     * @param site the site.
     * @return the style root resource for a given site.
     */
    public Resource getStyleRoot(CoralSession coralSession, SiteResource site)
        throws StyleException
    {
        Resource[] res = coralSession.
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
        res = coralSession.
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
     * @return the level resource.
     */
    public LevelResource addLevel(CoralSession coralSession, StyleResource style, LayoutResource layout, 
                                  int level, String description)
        throws StyleException
    {
        if(getLevel(coralSession, style,level) != null)
        {
            throw new StyleException("level already definied for the style");
        }
        LevelResource levelResource = null;
        try
        {
            levelResource = LevelResourceImpl.createLevelResource(coralSession, "" + level, style);
        }
        catch(InvalidResourceNameException e)
        {
            throw new RuntimeException("unexpected exception", e);
        }
        levelResource.setDescription(description);
        if(layout != null)
        {
            levelResource.setLayout(layout);
        }
        levelResource.update();
        return levelResource;
    }

    /** 
     * delete the level from the system.
     *
     * @param level the level to delete.
     */
    public void deleteLevel(CoralSession coralSession, LevelResource level)
        throws StyleException
    {
        try
        {
            coralSession.getStore().deleteResource(level);
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
    public LevelResource getLevel(CoralSession coralSession, StyleResource style, int level)
    {
        Resource[] resources = coralSession.getStore().getResource(style,""+level);
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
    public LevelResource[] getLevels(CoralSession coralSession, StyleResource style)
    {
        ArrayList<LevelResource> list = new ArrayList<LevelResource>();
        Resource[] resources = coralSession.getStore().getResource(style);
        for(int i = 0; i < resources.length; i++)
        {
            if(resources[i] instanceof LevelResource)
            {
                list.add((LevelResource)resources[i]);
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
    public String getLayout(CoralSession coralSession, StyleResource style, int level)
    {
        log.debug("looking for style - "+style.getName()+" and level - "+level);
        LevelResource levelResource = getLevel(coralSession, style, level);
        if(levelResource == null)
        {
            log.debug("level "+level+" not definied for style "+style.getName());
            if(level == 0)
            {
                if(style.getParent() instanceof StyleResource)
                {
                    // fallback to level 0 in parent style
                    return getLayout(coralSession, (StyleResource)style.getParent(),level);
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
                return getLayout(coralSession, style, level-1);
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
                    return getLayout(coralSession, (StyleResource)style.getParent(),level);
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
     * @param site the site.
     * @return layout resource.
     */
    public LayoutResource addLayout(CoralSession coralSession, String name, String description,
        SiteResource site)
        throws StyleException, AmbigousEntityNameException, InvalidResourceNameException
    {
        LayoutResource layout = null;
        if(getLayout(coralSession, site, name) != null)
        {
            throw new AmbigousEntityNameException("layout "+name+
                                            " already exists in site "+
                                            site.getName());
        }
        layout = LayoutResourceImpl.
            createLayoutResource(coralSession, name, getLayoutRoot(coralSession, site));
        layout.setDescription(description);
        layout.update();
        return layout;
    }
    
    /** 
     * delete the layout from the system.
     *
     * @param layout the layout to delete.
     */
    public void deleteLayout(CoralSession coralSession, LayoutResource layout)
        throws StyleException
    {
        try
        {
            coralSession.getStore().deleteResource(layout);
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
     */
    public void updateLayout(CoralSession coralSession, LayoutResource layout, String name,
        String description)
        throws StyleException, AmbigousEntityNameException, InvalidResourceNameException
    {
        SiteResource site = getSite(layout);
        if(!name.equals(layout.getName()))
        {
            if(getLayout(coralSession, site, name) != null)
            {
                throw new AmbigousEntityNameException("layout "+name+
                                                " already exists in site "+
                                                site.getName());
            }
            coralSession.getStore().setName(layout, name);
        }
        boolean update = false;
        if(!description.equals(layout.getDescription()))
        {
            layout.setDescription(description);
            update = true;
        }
        if(update)
        {
            layout.update();
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
    public LayoutResource getLayout(CoralSession coralSession, SiteResource site, String layout)
        throws StyleException
    {
        Resource[] res = coralSession.getStore().getResource(getLayoutRoot(coralSession, site), layout);
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
    public LayoutResource[] getLayouts(CoralSession coralSession, SiteResource site)
        throws StyleException
    {
        Resource[] result = coralSession.getStore().getResource(getLayoutRoot(coralSession, site));
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
    public Resource getLayoutRoot(CoralSession coralSession, SiteResource site)
        throws StyleException
    {
        Resource[] res = coralSession.getStore().getResource(site, "styles");
        if(res.length == 0)
        {
            throw new StyleException("styles root for site "+site.getName()+
                                     " not found");
        }
        if(res.length > 1)
        {
            throw new StyleException("multiple layout roots for site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], "layouts");
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
    public ComponentSocketResource[] getSockets(CoralSession coralSession, LayoutResource layout)
        throws StyleException
    {
        Resource[] res = coralSession.getStore().getResource(layout);
        ArrayList<ComponentSocketResource> temp = new ArrayList<ComponentSocketResource>();
        for(int i=0; i<res.length; i++)
        {
            if(res[i] instanceof ComponentSocketResource)
            {
                temp.add((ComponentSocketResource)res[i]);
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
     */
    public ComponentSocketResource addSocket(CoralSession coralSession, LayoutResource layout, 
                                             String name)
        throws StyleException
    {
        try
        {
            return ComponentSocketResourceImpl.
                createComponentSocketResource(coralSession, name, layout);
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
     */
    public void deleteSocket(CoralSession coralSession, LayoutResource layout, String name)
        throws StyleException
    {
        try
        {
            Resource[] res = coralSession.getStore().getResource(layout, name);
            ComponentSocketResource socket = (ComponentSocketResource)res[0];
            coralSession.getStore().deleteResource(socket);
        }
        catch(Exception e)
        {
            throw new StyleException("socket removal failed", e);
        }   
    }

    /**
     * Parses the velocity template and finds declared component sockets.
     *
     * @param templateContents
     * @return names of the declared sockets.
     */
    public String[] findSockets(String templateContents)
        throws StyleException
    {
        Reader r = new StringReader(templateContents);
        Writer w = new StringWriter();
        TemplatingContext context = templating.createContext();
        List<String> sockets = new ArrayList<String>();
        context.put("sockets", sockets);
        context.put("component", new FakeComponentTool(context));
        try
        {
            templating.merge(context, r, w, "<uploaded file>");
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
        private TemplatingContext context;
        
        public FakeComponentTool(TemplatingContext context)
        {
            this.context = context;
        }

        public void embed(String component, List<List<String>> params)
        {
            if(component.equals("CMSComponentWrapper"))
            {
                for(int i=0; i<params.size(); i++)
                {
                    if(params.get(i) instanceof List)
                    {
                        List<String> p = (List<String>)params.get(i);
                        if(p.get(0) instanceof String && p.get(0).equals("instance"))
                        {
                            List<String> sockets = (List<String>)context.get("sockets");
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
     * @param templateSockets list of socket names.
     * @return <code>true</code> if the sockets sets are identical.
     */
    public boolean matchSockets(CoralSession coralSession, LayoutResource layout, String[] templateSockets)
        throws StyleException
    {
        Set<String> s1 = new HashSet<String>();
        for (int i = 0; i < templateSockets.length; i++)
        {
            s1.add(templateSockets[i]);
        }
        ComponentSocketResource[] layoutSockets = getSockets(coralSession, layout); 
        Set<String> s2 = new HashSet<String>();
        for (int i = 0; i < layoutSockets.length; i++)
        {
            s2.add(layoutSockets[i].getName());
        }
        return s1.equals(s2);
    }
}

