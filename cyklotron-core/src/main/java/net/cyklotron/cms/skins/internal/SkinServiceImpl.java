package net.cyklotron.cms.skins.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.skins.ComponentVariantResourceImpl;
import net.cyklotron.cms.skins.LayoutResource;
import net.cyklotron.cms.skins.LayoutResourceImpl;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.ScreenVariantResourceImpl;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinResource;
import net.cyklotron.cms.skins.SkinResourceImpl;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.encodings.HTMLEntityEncoder;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.mail.MailSystem;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.utils.StringUtils;


/**
 * Provides skinning funcitonality.
 */
public class SkinServiceImpl
    implements SkinService
{
    // instance variables ////////////////////////////////////////////////////

    protected Templating templating;
    
    protected StructureService structureService;
    
    protected FileSystem fileSystem;

    protected IntegrationService integrationService;

    protected MailSystem mailSystem;

    protected Logger log;
    
    protected String templateEncoding;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public SkinServiceImpl(Logger logger, Templating templating, FileSystem fileSystem, 
        StructureService structureService, IntegrationService integrationService,
        MailSystem mailSystem)
    {
        this.log = logger;
        this.templating = templating;
        this.structureService = structureService;
        this.fileSystem = fileSystem;
        this.integrationService = integrationService;
        this.mailSystem = mailSystem;
    }

    // public interface //////////////////////////////////////////////////////

    // skins /////////////////////////////////////////////////////////////////

    /**
     * Returns the currently selected skin for a site.
     */
    public String getCurrentSkin(CoralSession coralSession, SiteResource site)
        throws SkinException
    {
        try
        {
            NavigationNodeResource rootNode = structureService.
                getRootNode(coralSession, site);
            return rootNode.getPreferences().get("site.skin","default");
        }
        catch(Exception e)
        {
            throw new SkinException("failed to lookup site's root node", e);
        }
    }

    /**
     * Selects a skin for a site.
     */
    public void setCurrentSkin(CoralSession coralSession, SiteResource site, String skin)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        try
        {
            NavigationNodeResource rootNode = structureService.
                getRootNode(coralSession, site);
            rootNode.getPreferences().set("site.skin", skin);
        }
        catch(Exception e)
        {
            throw new SkinException("failed to lookup site's root node", e);
        }
    }

    /**
     * Checks if the site has a skin with the given name.
     */
    public boolean hasSkin(CoralSession coralSession, SiteResource site, String skin)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if (res.length != 1)
        {
            throw new SkinException(
                "could not find skins node in site " + site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        return res.length == 1;
    }    

    /**
     * Returns a skin descriptor object.
     */
    public SkinResource getSkin(CoralSession coralSession, SiteResource site, String skin)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        return (SkinResource)res[0];
    }

    /**
     * Returns skins available for a given site.
     */
    public SkinResource[] getSkins(CoralSession coralSession, SiteResource site)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0]);
        SkinResource[] skins = new SkinResource[res.length];
        for(int i=0; i<skins.length; i++)
        {
            skins[i] = (SkinResource)res[i];
        }
        return skins;
    }

    /**
     * Creates a new skin
     * 
     * @param site the site to create skin for.
     * @param skin new skin's name.
     * @param source the skin to copy (possibly from another site), or null.
     * @param subject the subject that performs the operation.
     * @return newly created skin.
     * @throws SkinException if site by the requested name exists, or the operation
     * otherwise fails.
     */
    public SkinResource createSkin(CoralSession coralSession, SiteResource site, String skin, SkinResource source)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        Resource skinsNode = res[0];
        res = coralSession.getStore().getResource(skinsNode, skin);
        if(res.length != 0)
        {
            throw new SkinException("skin "+skin+" already exists in site "+site.getName());
        }
        try
        {
            if(source == null)
            {
                SkinResource skinRes = SkinResourceImpl.createSkinResource(coralSession, skin, skinsNode);
                CmsNodeResourceImpl.createCmsNodeResource(coralSession, "layouts", skinRes);
                CmsNodeResourceImpl.createCmsNodeResource(coralSession, "components", skinRes);
                CmsNodeResourceImpl.createCmsNodeResource(coralSession, "screens", skinRes);
                fileSystem.mkdirs("/content/cms/sites/"+site.getName()+"/"+skin);
                fileSystem.mkdirs("/templates/cms/sites/"+site.getName()+"/"+skin);
                return skinRes;
            }
            else
            {
                coralSession.getStore().copyTree(source, skinsNode, skin);
                res = coralSession.getStore().getResource(skinsNode, skin);
                SkinResource skinRes = (SkinResource)res[0];
                String sourceSite = source.getParent().getParent().getName();
                copyDir("/content/cms/sites/"+sourceSite+"/"+source.getName(),
                        "/content/cms/sites/"+site.getName()+"/"+skin);
                copyDir("/templates/cms/sites/"+sourceSite+"/"+source.getName(),
                        "/templates/cms/sites/"+site.getName()+"/"+skin);
                return skinRes;
            }
        }
        catch(Exception e)
        {
            throw new SkinException("failed to create skin", e);
        }
    }

    /**
     * Renames a skin.
     * 
     * <p>If the skin is currently enabled for the site, the setting is updated
     * accordingly.</p>
     * 
     * @param skin the skin to rename.
     * @param name the new name.
     * @throws SkinException if the skin has a sibling skin with a given name
     * or the opeartion otheriwse fails.
     */
    public void renameSkin(CoralSession coralSession, SkinResource skin, String name)
        throws SkinException
    {
        SiteResource site = (SiteResource)skin.getParent().getParent();
        boolean current = getCurrentSkin(coralSession, site).equals(skin.getName());
        if(hasSkin(coralSession, site, name))
        {
            throw new SkinException("skin "+name+" already exists in site "+site.getName());
        }
        try
        {
            fileSystem.rename("/content/cms/sites/"+site.getName()+"/"+skin.getName(), 
                "/content/cms/sites/"+site.getName()+"/"+name);
            fileSystem.rename("/templates/cms/sites/"+site.getName()+"/"+skin.getName(), 
                "/templates/cms/sites/"+site.getName()+"/"+name);
            coralSession.getStore().setName(skin, name);
            if(current)
            {
                setCurrentSkin(coralSession, site, name);
            } 
        }
        catch (Exception e)
        {
            throw new SkinException("failed to rename skin", e);
        }
    }
        
    /**
     * Deletes a skin.
     * 
     * @param skin the skin to delete.
     * @throws SkinException if the skin is currently enabled, or the operation
     * otherwise fails.
     */    
    public void deleteSkin(CoralSession coralSession, SkinResource skin)
        throws SkinException
    {
        SiteResource site = (SiteResource)skin.getParent().getParent();
        if(getCurrentSkin(coralSession, site).equals(skin.getName()))
        {
            throw new SkinException("cannon delete active skin");
        }
        try
        {
            coralSession.getStore().deleteTree(skin);
            deleteDir("/content/cms/sites/"+site.getName()+"/"+skin.getName());
            deleteDir("/templates/cms/sites/"+site.getName()+"/"+skin.getName());
        }
        catch (Exception e)
        {
            throw new SkinException("failed to delete skin", e);
        }
    }

    // layouts ///////////////////////////////////////////////////////////////

    /**
     * Returns layouts defined by the skin.
     */
    public LayoutResource[] getLayouts(CoralSession coralSession, SiteResource site, String skin)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }        
        res = coralSession.getStore().getResource(res[0]);
        LayoutResource[] layouts = new LayoutResource[res.length];
        for(int i=0; i<layouts.length; i++)
        {
            layouts[i] = (LayoutResource)res[i];
        }
        return layouts;
    }
    
    /**
     * Checks if the skin defines a layout wiht the given name.
     */
    public boolean hasLayoutTemplate(CoralSession coralSession, SiteResource site, String skin, String name)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }   
        res = coralSession.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], name);
        return (res.length == 1);
    }

    /**
     * Returns a layout template provided by the skin.
     */
    public Template getLayoutTemplate(CoralSession coralSession, SiteResource site, String skin, String name)
        throws TemplateNotFoundException, SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }   
        res = coralSession.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }
        log.debug("trying to find layout resource: "+name+" in path: "+res[0].getPath());
        res = coralSession.getStore().getResource(res[0], name);
        if(res.length != 1)
        {
            log.debug("layout resource '"+name+"' not found");
            RunData data = webcoreService.getRunData();
            try
            {
                return finderService.findTemplate(Assembler.LAYOUT, data, "cms", "emergency");
            }
            catch(Exception e)
            {
                throw new SkinException("failed to load emergency layout", e);
            }
        }
        log.debug("layout resource '"+name+"' found");
        String path = "/sites/"+site.getName()+"/"+skin+"/layouts/"+name;
        return templating.getTemplate(path);
    }

    // components ////////////////////////////////////////////////////////////

    /**
     * Returns visual variants available for a component.
     */
    public ComponentVariantResource[] getComponentVariants(CoralSession coralSession, SiteResource site, String skin,
                                                           String app, String component)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            // no components in this skin
            return new ComponentVariantResource[0];
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            // no component variants for the application in this skin
            return new ComponentVariantResource[0];
        }
        res = coralSession.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            // no variants for the comonent in this skin
            return new ComponentVariantResource[0];
        }
        res = coralSession.getStore().getResource(res[0]);
        ComponentVariantResource[] vars = new ComponentVariantResource[res.length];
        for(int i=0; i<vars.length; i++)
        {
            vars[i] = (ComponentVariantResource)res[i];
        }
        return vars;
    }
                   
    /**
     * Returns visual variant of a component.
     */
    public ComponentVariantResource getComponentVariant(CoralSession coralSession, SiteResource site, String skin,
                                                           String app, String component, String variant)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            throw new SkinException("components nod in skin "+skin+" not present in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            throw new SkinException("no component variants for the application "+app+" in skin "+skin+" for site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            throw new SkinException("no component variants for the component "+component+" in application "+app+" in skin "+skin+" for site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            throw new SkinException("variant "+variant+" for the component "+component+" in application "+app+" in skin "+skin+" for site "+site.getName()+" is missing");
        }        
        return (ComponentVariantResource)res[0];                       
    }
                                 
    /**
     * Checks if the skin defines component variand with the given name.
     */
    public boolean hasComponentVariant(CoralSession coralSession, SiteResource site, String skin,
                                       String app, String component, 
                                       String variant)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = coralSession.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            return false;
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            return false;
        }
        res = coralSession.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            return false;
        }
        res = coralSession.getStore().getResource(res[0], variant);
        return (res.length == 1);
    }

    /**
     * Creates a new variant of a component.
     */
    public ComponentVariantResource createComponentVariant(CoralSession coralSession, SiteResource site, 
        String skin, String app, String component, String variant, 
        Subject subject)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        Resource p = res[0];  
        res = coralSession.getStore().getResource(p, "components");
        if(res.length == 0)
        {
            p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, "components", p);
        }
        else
        {
            p = res[0];
        }
        res = coralSession.getStore().getResource(p, app);
        if(res.length == 0)
        {
            p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, app, p);
        }
        else
        {
            p = res[0];
        }
        res = coralSession.getStore().getResource(p, component);
        if(res.length == 0)
        {
            p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, component, p);
        }
        else
        {
            p = res[0];
        }
        res = coralSession.getStore().getResource(p, variant);
        if(res.length != 0)
        {
            throw new SkinException("variant "+variant+" already exists for component "+
                component+" in skin "+skin+" for site "+site.getName());
        }
        else
        {
            return ComponentVariantResourceImpl.createComponentVariantResource(coralSession, variant, p);
        }
    }

    /**
     * Deletes a component variant;
     */
    public void deleteComponentVariant(CoralSession coralSession, SiteResource site, String skin, 
        String app, String component, String variant)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = coralSession.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            throw new SkinException("could not find find components node in skin "+skin+
                                    " for site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " provides no variants for application "+app+
                                    " components");
        }
        res = coralSession.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " provides no variants for component "+
                                    component+" in application "+app);
        }
        res = coralSession.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " does not provide variant "+variant+
                                    " for component "+ component+
                                    " in application "+app);            
        }
        try
        {
            coralSession.getStore().deleteResource(res[0]);
        }
        catch (EntityInUseException e)
        {
            throw new SkinException("unexpected exception", e);
        }
    }
     
    /**
     * Return a component variant template provided by the skin.
     */
    public Template getComponentTemplate(CoralSession coralSession, SiteResource site, String skin,
                                         String app, String component, 
                                         String variant, String state)
        throws TemplateNotFoundException, SkinException
    {
        String path = getComponentTemplatePath(coralSession, site, skin, 
                                               app, component, 
                                               variant, state, true);
        return templating.getTemplate(path);
    }

    /**
     * Checks if a screen template provided by the skin.
     */
    public boolean hasComponentTemplate(CoralSession coralSession, SiteResource site, String skin,
                                     String app, String component, 
                                     String variant, String state)
        throws SkinException
    {
        String path = getComponentTemplatePath(coralSession, site, skin, 
                                               app, component, 
                                               variant, state, false);
        if(path != null)
        {
            return templating.templateExists(path);
        }
        else
        {
            return false;
        }
    }

    protected void invalidateComponentTemplate(CoralSession coralSession, SiteResource site, String skin,
        String app, String component, String variant, String state)
        throws SkinException
    {
        String path = getComponentTemplatePath(coralSession, site, skin, 
            app, component, 
            variant, state, true);

        templating.invalidateTemplate(path);
    }
    
    /**
     * Returns finder path of a component.
     */
    public String getComponentTemplatePath(CoralSession coralSession, SiteResource site, String skin,
                                           String app, String component, 
                                           String variant, String state, 
                                           boolean critical)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = coralSession.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            if(critical)
            {
                throw new SkinException("could not find find components node in skin "+skin+
                                        " for site "+site.getName());
            }
            else
            {
                return null;
            }
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            if(critical)
            {
                throw new SkinException("skin "+skin+" in site "+site.getName()+
                                        " provides no variants for application "+app+
                                        " components");
            }
            else
            {
                return null;
            }
        }
        res = coralSession.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            if(critical)
            {
                throw new SkinException("skin "+skin+" in site "+site.getName()+
                                        " provides no variants for component "+
                                        component+" in application "+app);
            }
            else
            {
                return null;
            }
        }
        res = coralSession.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            if(critical)
            {
                throw new SkinException("skin "+skin+" in site "+site.getName()+
                                        " does not provide variant "+variant+
                                        " for component "+ component+
                                        " in application "+app);            
            }
            else
            {
                return null;
            }
        }

        ComponentResource integComp = integrationService.getComponent(coralSession, app, component);
        if(integComp == null)
        {
            throw new SkinException("application "+app+" does not provide component"+
                                    component);
        }
        String integState = StringUtils.
                    foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        if(!((state.equalsIgnoreCase("Default") 
              && integrationService.getComponentStates(coralSession, integComp).length == 0) 
             || integrationService.hasState(coralSession, integComp, integState)))
        {
            throw new SkinException("component "+component+" in application "+app+
                                   " does not provide state "+state);
        }
        
        int i = component.lastIndexOf(',');
        String packagePart = null;
        String namePart = null;
        if(i > 0)
        {
            packagePart = component.substring(0,i).replace(',','/');
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, component.substring(i+1));
        }
        else
        {
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, component);
        }
        String variantPart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, variant);
        String statePart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        String path = 
            "/sites/"+site.getName()+
            "/"+skin+"/components/"+
            app+
            "/"+(packagePart != null ? (packagePart+"/") : "")+
            namePart+ 
            (variantPart.equals("default") ? "" : ("_"+variantPart))+
            (statePart.equals("default") ? "" : ("_"+statePart));

        return path;
    }

    // screens ///////////////////////////////////////////////////////////////

    /**
     * Returns visual variants available for a screen.
     */
    public ScreenVariantResource[] getScreenVariants(CoralSession coralSession, SiteResource site, String skin,
                                                           String app, String screen)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            // no screens in this skin
            return new ScreenVariantResource[0];
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            // no screen variants for the application in this skin
            return new ScreenVariantResource[0];
        }
        res = coralSession.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            // no variants for the comonent in this skin
            return new ScreenVariantResource[0];
        }
        res = coralSession.getStore().getResource(res[0]);
        ScreenVariantResource[] vars = new ScreenVariantResource[res.length];
        for(int i=0; i<vars.length; i++)
        {
            vars[i] = (ScreenVariantResource)res[i];
        }
        return vars;
    }

    /**
     * Checks if the skin defines screen variand with the given name.
     */
    public boolean hasScreenVariant(CoralSession coralSession, SiteResource site, String skin,
                                       String app, String screen, 
                                       String variant)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = coralSession.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            return false;
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            return false;
        }
        res = coralSession.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            return false;
        }
        res = coralSession.getStore().getResource(res[0], variant);
        return (res.length == 1);
    }
     
    /**
     * Returns visual variant of a screen.
     */
    public ScreenVariantResource getScreenVariant(CoralSession coralSession, SiteResource site, String skin,
                                                           String app, String screen, String variant)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            throw new SkinException("screens nod in skin "+skin+" not present in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            throw new SkinException("no screen variants for the application "+app+" in skin "+skin+" for site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            throw new SkinException("no screen variants for the screen "+screen+" in application "+app+" in skin "+skin+" for site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            throw new SkinException("variant "+variant+" for the screen "+screen+" in application "+app+" in skin "+skin+" for site "+site.getName()+" is missing");
        }        
        return (ScreenVariantResource)res[0];                       
    }
     
    /**
     * Return a screen variant template provided by the skin.
     */
    public Template getScreenTemplate(CoralSession coralSession, SiteResource site, String skin,
                                      String app, String screen, 
                                      String variant, String state)
        throws TemplateNotFoundException, SkinException
    {
        String path = getScreenTemplatePath(coralSession, site, skin, app, screen,
                                            variant, state, true);
        return templating.getTemplate(path);
    }


    /**
     * Checks if a screen template provided by the skin.
     */
    public boolean hasScreenTemplate(CoralSession coralSession, SiteResource site, String skin,
                                     String app, String screen, 
                                     String variant, String state)
        throws SkinException
    {
        String path = getScreenTemplatePath(coralSession, site, skin, app, screen,
                                            variant, state, false);
        if(path != null)
        {
            return templating.templateExists(path);
        }
        else
        {
            return true;
        }
    }

    protected void invalidateScreenTemplate(CoralSession coralSession, SiteResource site, String skin, String app, String screen,
        String variant, String state) throws SkinException
    {
        String path = getScreenTemplatePath(coralSession, site, skin, app, screen, variant, state, true);
        templating.invalidateTemplate(path);
    }
    
    /**
     * Returns finder path of a screen.
     */
    public String getScreenTemplatePath(CoralSession coralSession, SiteResource site, String skin,
                                        String app, String screen, 
                                        String variant, String state, 
                                        boolean critical)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = coralSession.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            if(critical)
            {
                throw new SkinException("could not find find screens node in skin "+skin+
                                        " for site "+site.getName());
            }
            else
            {
                return null;
            }
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            if(critical)
            {
                throw new SkinException("skin "+skin+" in site "+site.getName()+
                                        " provides no variants for application "+app+" screens");
            }
            else
            {
                return null;
            }
        }
        res = coralSession.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            if(critical)
            {
                throw new SkinException("skin "+skin+" in site "+site.getName()+
                                        " provides no variants for screen "+
                                        screen+" in application "+app);
            }
            else
            {
                return null;
            }
        }
        res = coralSession.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            if(critical)
            {
                throw new SkinException("skin "+skin+" in site "+site.getName()+
                                        " does not provide variant "+variant+
                                        " for screen "+ screen+
                                        " in application "+app);            
            }
            else
            {
                return null;
            }
        }

        ScreenResource integScreen = integrationService.getScreen(coralSession, app, screen);
        if(integScreen == null)
        {
            throw new SkinException("application "+app+" does not provide screen"+
                                    screen);
        }
        if(!((state.equalsIgnoreCase("Default") 
              && integrationService.getScreenStates(coralSession, integScreen).length == 0) 
             || integrationService.hasState(coralSession, integScreen, state)))
        {
            throw new SkinException("screen "+screen+" in application "+app+
                                   " does not provide state "+state);
        }
        
        int i = screen.lastIndexOf(',');
        String packagePart = null;
        String namePart = null;
        if(i > 0)
        {
            packagePart = screen.substring(0,i).replace(',','/');
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, screen.substring(i+1));
        }
        else
        {
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, screen);
        }
        String variantPart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, variant);
        String statePart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        String path = 
            "/sites/"+site.getName()+
            "/"+skin+"/screens/"+
            app+
            "/"+(packagePart != null ? (packagePart+"/") : "")+
            namePart+ 
            (variantPart.equals("default") ? "" : ("_"+variantPart))+
            (statePart.equals("default") ? "" : ("_"+statePart));

        return path;
    }

    /**
     * Lists the directories in the skins's static content located in the
     * directory at the given path.
     */
    public String[] getContentDirectoryNames(SiteResource site, String skin, 
                                             String path)
        throws SkinException
    {
        return getContentNames(site, skin, path, true);
    }
    
    /**
     * Lists the files in the skins's static content located in the
     * directory at the given path.
     */
    public String[] getContentFileNames(SiteResource site, String skin, 
                                        String path)
        throws SkinException
    {
        return getContentNames(site, skin, path, false);
    }

    public boolean contentItemExists(SiteResource site, String skin, String path)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);
        return fileSystem.exists(filePath);
    }

	// layouts

	/**
	 * Create a new layout template in the skin.
	 * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param layout the layout.
	 * @param contents the contents of the layout template.
	 * @throws SkinException if the operation fails.
	 */
	public void createLayoutTemplate(CoralSession coralSession, SiteResource site, String skin, String layout, 
		String contents, Subject subject)
		throws SkinException
	{
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }   
        res = coralSession.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }
        log.debug("trying to find layout resource: "+layout+" in path: "+res[0].getPath());
        Resource parent = res[0];
        res = coralSession.getStore().getResource(parent, layout);
        if(res.length > 0)
        {
            throw new SkinException("layout "+layout+" already exists in skin "+
                skin+" for site "+site.getName());
        }
		String path = getLayoutTemplatePath(site, skin, layout);
		if(fileSystem.exists(path))
		{
            throw new SkinException("refusing to overwrite "+path);
		}
        try
        {
            LayoutResourceImpl.createLayoutResource(coralSession, layout, parent);
        }
        catch(Exception e)
        {
            throw new SkinException("failed to create layout resource", e);
        }
        
        writeTemplate(path, contents, "failed to create layout");
        invalidateLayoutTemplate(site, skin, layout);
	}
	
	/**
	 * Removes a layout template from the skin.
	 * 
	 * @param site the site.
	 * @param skin the skin.
	 * @param layout the layout.
	 * @throws SkinException if the operation fails.
	 */
	public void deleteLayoutTemplate(CoralSession coralSession, SiteResource site, String skin, String layout)
		throws SkinException
	{
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }   
        res = coralSession.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }
        log.debug("trying to find layout resource: "+layout+" in path: "+res[0].getPath());
        res = coralSession.getStore().getResource(res[0], layout);
        if(res.length == 0)
        {
            throw new SkinException("layout "+layout+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            coralSession.getStore().deleteResource(res[0]);
        }
        catch(Exception e)
        {
            throw new SkinException("failed to delete layout resource", e);
        }
		String path = getLayoutTemplatePath(site, skin, layout);
		if(!fileSystem.exists(path))
		{
			throw new SkinException(path+" does not exist");
		}
		try
		{
			fileSystem.delete(path);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to delete layout", e);
		}		
        invalidateLayoutTemplate(site, skin, layout);
	}
    	
	/**
	 * Returns the contents of a layout template
	 *  
	 * @param site the site.
	 * @param skin the skin.
	 * @param layout the layout.
	 * @return the contents of the template.
	 * @throws SkinException if the operation fails.
	 */
	public String getLayoutTemplateContents(SiteResource site, String skin, 
		String layout)
		throws SkinException
	{
		String path = getLayoutTemplatePath(site, skin, layout);
		if(!fileSystem.exists(path))
		{
			throw new SkinException("layout "+layout+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			return fileSystem.read(path, templateEncoding);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to retrieve layout template contents", e);
		}		
	}
    
    /**
     * Writes the contents af a layout template into a stream.
     *  
     * @param site the site.
     * @param skin the skin.
     * @param layout the layout.
     * @param out the stream to write contents to.
     * @throws SkinException if the operation fails.
     */
    public void getLayoutTemplateContents(SiteResource site, String skin, 
        String layout, OutputStream out)
        throws SkinException
    {
        String path = getLayoutTemplatePath(site, skin, layout);
        if(!fileSystem.exists(path))
        {
            throw new SkinException("layout "+layout+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileSystem.read(path, out);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to retrieve layout template contents", e);
        }       
    }

    /**
     * Return layout template file size.
     * 
     * @param site the site.
     * @param skin the skin.
     * @param path the content file path.
     */
    public long getLayoutTemplateLength(SiteResource site, String skin, String layout)
        throws SkinException
    {
        String path = getLayoutTemplatePath(site, skin, layout);
        if(!fileSystem.exists(path))
        {
            throw new SkinException("layout "+layout+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        else
        {
            return fileSystem.length(path);
        }
    }
    
	/**
	 * Changes the contents of a skin template.
	 *  
	 * @param site the site.
	 * @param skin the skin.
	 * @param layout the layout.
	 * @param contents the contents of the layout template.
	 * @throws SkinException if the operation fails.
	 */
	public void setLayoutTemplateContents(SiteResource site, String skin, String layout,
		String contents)
		throws SkinException
	{
		String path = getLayoutTemplatePath(site, skin, layout);
		if(!fileSystem.exists(path))
		{
			throw new SkinException("layout "+layout+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
        writeTemplate(path, contents, "failed to modify layout template contents");
        invalidateLayoutTemplate(site, skin, layout);
	}

	// components

	public void createComponentTemplate(CoralSession coralSession, SiteResource site, String skin, 
		String app, String component, String variant, String state, 
        String contents)
		throws SkinException
	{
		String path = getComponentTemplatePath(site, skin, app, component, 
			variant, state);
		if(fileSystem.exists(path))
		{
			throw new SkinException("component "+app+":"+component+" variant "+
				variant+" already exists in skin "+
				skin+" for site "+site.getName());
		}
        writeTemplate(path, contents, "failed to create layout");
        invalidateComponentTemplate(coralSession, site, skin, app, component, variant, state);
	}
    	
	public void deleteComponentTemplate(CoralSession coralSession, SiteResource site, String skin, 
		String app,	String component, String variant, String state)
		throws SkinException
	{
		String path = getComponentTemplatePath(site, skin, app, component,
			variant, state);
		if(!fileSystem.exists(path))
		{
			throw new SkinException("component "+app+":"+component+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			fileSystem.delete(path);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to delete layout", e);
		}				
        invalidateComponentTemplate(coralSession, site, skin, app, component, variant, state);
	}
    
	public String getComponentTemplateContents(SiteResource site, String skin, 
		String app,  String component, String variant, String state)
		throws SkinException
	{
		String path = getComponentTemplatePath(site, skin, app, component,
			variant, state);
		if(!fileSystem.exists(path))
		{
			throw new SkinException("component "+app+":"+component+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			return fileSystem.read(path, templateEncoding);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to retrieve layout template contents", e);
		}		
	}

    public void getComponentTemplateContents(SiteResource site, String skin, 
        String app,  String component, String variant, String state,
        OutputStream out)
        throws SkinException
    {
        String path = getComponentTemplatePath(site, skin, app, component,
            variant, state);
        if(!fileSystem.exists(path))
        {
            throw new SkinException("component "+app+":"+component+" variant "+
                variant+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileSystem.read(path, out);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to retrieve layout template contents", e);
        }       
    }

    protected String getComponentTemplatePath(String app, String component, String state)
    {
        int i = component.lastIndexOf(',');
        String packagePart = null;
        String namePart = null;
        if(i > 0)
        {
            packagePart = component.substring(0,i).replace(',','/');
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, component.substring(i+1));
        }
        else
        {
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, component);
        }
        String statePart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        String path = 
            "/components/"+
            (packagePart != null ? (packagePart+"/") : "")+
            namePart+ 
            (statePart.equals("default") ? "" : ("_"+statePart))+
            ".vt";
         return path;
    }

    public List getComponentTemplateLocales(String app, String component, String state)
        throws SkinException
    {
        List list = new ArrayList();
        String suffix = getComponentTemplatePath(app, component, state);
        List supportedLocales = webcoreService.getSupportedLocales();
        for (Iterator i = supportedLocales.iterator(); i.hasNext();)
        {
            Locale l = (Locale)i.next();
            if(fileSystem.exists("/templates/"+app+"/"+
                l.toString()+"_HTML"+suffix))
            {
                list.add(l);
            }
        }
        return list;
    }
    
    public String getComponentTemplateContents(String app, String component, String state, Locale locale)
        throws SkinException
    {
        String path =
            "/templates/"+
            app+"/"+
            locale.toString()+"_HTML"+
            getComponentTemplatePath(app, component, state);
        try
        {
            return fileSystem.read(path, templateEncoding);
        }
        catch(Exception e)
        {
            throw new SkinException("failed to read template contents", e);
        }
    }
    	
	public void setComponentTemplateContents(CoralSession coralSession, SiteResource site, String skin, 
		String app, String component, String variant, String state, 
        String contents)
		throws SkinException
	{
		String path = getComponentTemplatePath(site, skin, app, component,
			variant, state);
		if(!fileSystem.exists(path))
		{
			throw new SkinException("component "+app+":"+component+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
        
        writeTemplate(path, contents, "failed to modify layout template contents");
        invalidateComponentTemplate(coralSession, site, skin, app, component, variant, state);
	}

    public long getComponentTemplateLength(SiteResource site, String skin, 
        String app,  String component, String variant, String state)
        throws SkinException
    {
        String path = getComponentTemplatePath(site, skin, app, component,
            variant, state);
        if(!fileSystem.exists(path))
        {
            throw new SkinException("component "+app+":"+component+" variant "+
                variant+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        return fileSystem.length(path);
    }

	// screens

    /**
     * Creates a new variant of a screen.
     */
    public ScreenVariantResource createScreenVariant(CoralSession coralSession, SiteResource site, 
        String skin, String app, String screen, String variant)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        Resource p = res[0];  
        res = coralSession.getStore().getResource(p, "screens");
        if(res.length == 0)
        {
            p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, "screens", p);
        }
        else
        {
            p = res[0];
        }
        res = coralSession.getStore().getResource(p, app);
        if(res.length == 0)
        {
            p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, app, p);
        }
        else
        {
            p = res[0];
        }
        res = coralSession.getStore().getResource(p, screen);
        if(res.length == 0)
        {
            p = CmsNodeResourceImpl.createCmsNodeResource(coralSession, screen, p);
        }
        else
        {
            p = res[0];
        }
        res = coralSession.getStore().getResource(p, variant);
        if(res.length != 0)
        {
            throw new SkinException("variant "+variant+" already exists for screen "+
                screen+" in skin "+skin+" for site "+site.getName());
        }
        else
        {
            return ScreenVariantResourceImpl.createScreenVariantResource(coralSession, variant, p);
        }
    }

   /**
     * Deletes a screen variant;
     */
    public void deleteScreenVariant(CoralSession coralSession, SiteResource site, String skin, 
        String app, String screen, String variant)
        throws SkinException
    {
        Resource[] res = coralSession.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = coralSession.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            throw new SkinException("could not find find screens node in skin "+skin+
                                    " for site "+site.getName());
        }
        res = coralSession.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " provides no variants for application "+app+
                                    " screens");
        }
        res = coralSession.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " provides no variants for screen "+
                                    screen+" in application "+app);
        }
        res = coralSession.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " does not provide variant "+variant+
                                    " for screen "+ screen+
                                    " in application "+app);            
        }
        try
        {
            coralSession.getStore().deleteResource(res[0]);
        }
        catch (EntityInUseException e)
        {
            throw new SkinException("unexpected exception", e);
        }
    }

	public void createScreenTemplate(CoralSession coralSession, SiteResource site, String skin, 
		String app, String screen, String variant, String state, String contents)
		throws SkinException
	{
		String path = getScreenTemplatePath(site, skin, app, screen, 
			variant, state);
		if(fileSystem.exists(path))
		{
			throw new SkinException("screen "+app+":"+screen+" variant "+
				variant+" already exists in skin "+
				skin+" for site "+site.getName());
		}
        
        writeTemplate(path, contents, "failed to create layout");
        invalidateScreenTemplate(coralSession, site, skin, app, screen, variant, state);	
    }
    	
	public void deleteScreenTemplate(CoralSession coralSession, SiteResource site, String skin, 
		String app,	String screen, String variant, String state)
		throws SkinException
	{
		String path = getScreenTemplatePath(site, skin, app, screen,
			variant, state);
		if(!fileSystem.exists(path))
		{
			throw new SkinException("screen "+app+":"+screen+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			fileSystem.delete(path);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to delete layout", e);
		}				
        invalidateScreenTemplate(coralSession, site, skin, app, screen, variant, state);  
	}

    protected String getScreenTemplatePath(String app, String screen, String state)
    {
        int i = screen.lastIndexOf(',');
        String packagePart = null;
        String namePart = null;
        if(i > 0)
        {
            packagePart = screen.substring(0,i).replace(',','/');
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, screen.substring(i+1));
        }
        else
        {
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, screen);
        }
        String statePart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        String path = 
            "/screens/"+
            (packagePart != null ? (packagePart+"/") : "")+
            namePart+ 
            (statePart.equals("default") ? "" : ("_"+statePart))+
            ".vt";
         return path;
    }
    
    public List getScreenTemplateLocales(String app, String screen, String state)
        throws SkinException
    {
        List list = new ArrayList();
        String suffix = getScreenTemplatePath(app, screen, state);
        List supportedLocales = webcoreService.getSupportedLocales();
        for (Iterator i = supportedLocales.iterator(); i.hasNext();)
        {
            Locale l = (Locale)i.next();
            if(fileSystem.exists("/templates/"+app+"/"+
                l.toString()+"_HTML"+suffix))
            {
                list.add(l);
            }
        }
        return list;
    }
    
    public String getScreenTemplateContents(String app, String screen, String state, Locale locale)
        throws SkinException
    {
        String path =
            "/templates/"+
            app+"/"+
            locale.toString()+"_HTML"+
            getScreenTemplatePath(app, screen, state);
        try
        {
            return fileSystem.read(path, templateEncoding);
        }
        catch(Exception e)
        {
            throw new SkinException("failed to read template contents", e);
        }
    }

    public long getScreenTemplateLength(SiteResource site, String skin, 
        String app,  String screen, String variant, String state)
        throws SkinException
    {
        String path = getScreenTemplatePath(site, skin, app, screen,
            variant, state);
        if(!fileSystem.exists(path))
        {
            throw new SkinException("screen "+app+":"+screen+" variant "+
                variant+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        return fileSystem.length(path);
    }

    public void getScreenTemplateContents(SiteResource site, String skin, 
        String app,  String screen, String variant, String state,
        OutputStream out)
        throws SkinException
    {
        String path = getScreenTemplatePath(site, skin, app, screen,
            variant, state);
        if(!fileSystem.exists(path))
        {
            throw new SkinException("screen "+app+":"+screen+" variant "+
                variant+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileSystem.read(path, out);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to retrieve layout template contents", e);
        }       
    }

	public String getScreenTemplateContents(SiteResource site, String skin, 
		String app,  String screen, String variant, String state)
		throws SkinException
	{
		String path = getScreenTemplatePath(site, skin, app, screen,
			variant, state);
		if(!fileSystem.exists(path))
		{
			throw new SkinException("screen "+app+":"+screen+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			return fileSystem.read(path, templateEncoding);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to retrieve layout template contents", e);
		}		
	}
    	
	public void setScreenTemplateContents(CoralSession coralSession, SiteResource site, String skin, 
		String app, String screen, String variant, String state, String contents)
		throws SkinException
	{
		String path = getScreenTemplatePath(site, skin, app, screen,
			variant, state);
		if(!fileSystem.exists(path))
		{
			throw new SkinException("screen "+app+":"+screen+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
        
        writeTemplate(path, contents, "failed to modify layout template contents");
        invalidateScreenTemplate(coralSession, site, skin, app, screen, variant, state);  
	}

    // static content
    
    /**
     * Return static conent file MIME type.
     *
     * <p>This implementation guesses the type by file extension using 
     * {@see MailSystem#getContentType(String)}.</p>
     * 
     * @param site the site.
     * @param skin the skin.
     * @param path the content file path.
     */
    public String getContentFileType(SiteResource site, String skin, String path)
    {
        return mailSystem.getContentType(path);
    }
    
    public long getContentFileLength(SiteResource site, String skin, String path)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(fileSystem.exists(filePath))
        {
            return fileSystem.length(filePath);        
        }
        else
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
    }

    public void createContentFile(SiteResource site, String skin, String path, 
        InputStream data)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        try
        {
            fileSystem.write(filePath, data);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to create file", e);
        }
    }

    public void getContentFileContents(SiteResource site, String skin, 
        String path, OutputStream out)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(!fileSystem.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileSystem.read(filePath, out);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to load file contents", e);
        }                       
    }
        
    /**
     * Returns the contents of a content file as a string in the specified
     * encoding.
     * 
     * @param site the site.
     * @param skin she skin.
     * @param path content file path.
     * @param out the stream to write data to.
     * @param encoding requested character encoding.
     */
    public String getContentFileContents(SiteResource site, String skin, 
        String path, String encoding)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(!fileSystem.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            return fileSystem.read(filePath, encoding);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to load file contents", e);
        }                       
    }   
        
    public void setContentFileContents(SiteResource site, String skin, 
        String path, InputStream in)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(!fileSystem.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileSystem.write(filePath, in);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to modify file contents", e);
        }                       
    }
    
    public void setContentFileContents(SiteResource site, String skin, 
        String path, String contents, String encoding)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(!fileSystem.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileSystem.write(filePath, contents, encoding);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to modify file contents", e);
        }                       
    }
    
    public void deleteContentFile(SiteResource site, String skin, String path)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(!fileSystem.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileSystem.delete(filePath);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to delete file", e);
        }               
    }

    public void createContentDirectory(SiteResource site, String skin, 
        String path)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(fileSystem.exists(filePath))
        {
            throw new SkinException("directory "+path+" already exists");
        }
        try
        {
            fileSystem.mkdirs(filePath);
        }
        catch(Exception e)
        {
            throw new SkinException("failed to create directory", e);
        }                       
    }
        
    public void deleteContentDirectory(SiteResource site, String skin, 
        String path)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(!fileSystem.exists(filePath))
        {
            throw new SkinException("directory "+path+
                " does not exist in skin "+skin+" for site "+site.getName());
        }
        try
        {
            fileSystem.delete(filePath);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to delete directory", e);
        }        
    }

    // implementation ////////////////////////////////////////////////////////

	protected String getLayoutTemplatePath(SiteResource site, String skin, 
		String layout)
	{
		return "/templates/cms/sites/"+site.getName()+"/"+skin+"/layouts/"+
			layout+".vt";
	}
    
    protected void invalidateLayoutTemplate(SiteResource site, String skin, String layout)
    {
        String path = "/sites/"+site.getName()+"/"+skin+"/layouts/"+layout;
        templating.invalidateTemplate(path);
    }

    public String getTemplateFilename(String item, String state, String variant)
    {
        int i = item.lastIndexOf(',');
        String namePart = null;
        if(i > 0)
        {
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, item.substring(i+1));
        }
        else
        {
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, item);
        }
        String variantPart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, variant);
        String statePart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        String name = 
            namePart+ 
            (variantPart.equals("default") ? "" : ("_"+variantPart))+
            (statePart.equals("default") ? "" : ("_"+statePart))+
            ".vt";
        return name;
    }

	protected String getComponentTemplatePath(SiteResource site, String skin, 
		String app, String component, String variant, String state)
	{
        int i = component.lastIndexOf(',');
        String packagePart = null;
        String namePart = null;
        if(i > 0)
        {
            packagePart = component.substring(0,i).replace(',','/');
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, component.substring(i+1));
        }
        else
        {
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, component);
        }
        String variantPart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, variant);
        String statePart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        String path = 
            "/templates/cms/sites/"+site.getName()+
            "/"+skin+"/components/"+
            app+
            "/"+(packagePart != null ? (packagePart+"/") : "")+
            namePart+ 
            (variantPart.equals("default") ? "" : ("_"+variantPart))+
            (statePart.equals("default") ? "" : ("_"+statePart))+
            ".vt";

        return path;
	}

	protected String getScreenTemplatePath(SiteResource site, String skin, 
		String app, String screen, String variant, String state)
	{
        int i = screen.lastIndexOf(',');
        String packagePart = null;
        String namePart = null;
        if(i > 0)
        {
            packagePart = screen.substring(0,i).replace(',','/');
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, screen.substring(i+1));
        }
        else
        {
            namePart = StringUtils.
                foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, screen);
        }
        String variantPart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, variant);
        String statePart = StringUtils.
            foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        String path = 
            "/templates/cms/sites/"+site.getName()+
            "/"+skin+"/screens/"+
            app+
            "/"+(packagePart != null ? (packagePart+"/") : "")+
            namePart+ 
            (variantPart.equals("default") ? "" : ("_"+variantPart))+
            (statePart.equals("default") ? "" : ("_"+statePart))
            +".vt";

        return path;
	}

    protected String getContentPath(SiteResource site, String skinName, String path)
    {
        if(path.length() == 0 || path.charAt(0) != '/')
        {
            path = "/"+path;
        }
        path = "/content/cms/sites/"+site.getName()+"/"+skinName+path;
        return path;
    }

    protected String[] getContentNames(SiteResource site, String skinName, String path,
                                       boolean directories)
        throws SkinException
    {
    	
        String dir = getContentPath(site, skinName, path);
        if(!fileSystem.exists(dir))
        {
            throw new SkinException(dir+" does not exist");
        }
        try
        {
            String[] children = fileSystem.list(dir);
            if(children == null)
            {
            	return null;
            }
            ArrayList temp = new ArrayList(children.length);
            for(int i=0; i<children.length; i++)
            {
                if(fileSystem.isDirectory(dir+"/"+children[i]) == directories)
                {
                    temp.add(children[i]);
                }
            }
            String[] result = new String[temp.size()];
            temp.toArray(result);
            return result;
        }
        catch(IOException e)
        {
            throw new SkinException("IOException occured",e);
        }
    }
    
    protected void copyDir(String src, String dst)
        throws Exception
    {
        if(!fileSystem.exists(src))
        {
            throw new IOException("source directory "+src+" does not exist");
        }
        if(!fileSystem.canRead(src))
        {
            throw new IOException("source directory "+src+" is not readable");
        }
        if(!fileSystem.isDirectory(src))
        {
            throw new IOException(src+" is not a directory");
        }
        fileSystem.mkdirs(dst);
        String[] srcFiles = fileSystem.list(src);
        for(int i=0; i<srcFiles.length; i++)
        {
            String name = srcFiles[i];
            if(name.startsWith(".") || name.equals("CVS"))
            {
                continue;
            }
            if(fileSystem.isDirectory(src+"/"+name))
            {
                copyDir(src+"/"+name, dst+"/"+name);
            }
            else
            {
                fileSystem.copyFile(src+"/"+name, dst+"/"+name);
            }
        }
    }

    /**
     * @param string
     */
    private void deleteDir(String path)
        throws Exception
    {
        ArrayList stack = new ArrayList();
        ArrayList order = new ArrayList();
        stack.add(path);
        while(stack.size() > 0)
        {
            path = (String)stack.remove(stack.size()-1);
            if(fileSystem.isDirectory(path))
            {
                String[] children = fileSystem.list(path);
                for(int i=0; i<children.length; i++)
                {
                    stack.add(path+"/"+children[i]);
                }
            }
            order.add(path);
        }
        while(order.size() > 0)
        {
            path = (String)order.remove(order.size()-1);
            fileSystem.delete(path);
        }
    }
    
    private void writeTemplate(String path, String contents, String message)
        throws SkinException
    {
        try
        {
            HTMLEntityEncoder encoder = new HTMLEntityEncoder();
            if(!fileSystem.exists(path))
            {
                fileSystem.mkdirs(StringUtils.directoryPath(path));
            }
            String encoded = contents.length() > 0 ?
                encoder.encodeHTML(contents, templateEncoding) :
                contents;
            fileSystem.write(path, encoded, templateEncoding);
        }
        catch(Exception e)
        {
            throw new SkinException(message, e);
        }
    }
}

