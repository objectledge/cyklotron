package net.cyklotron.cms.skins.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import pl.caltha.encodings.HTMLEntityEncoder;

import net.labeo.services.BaseService;
import net.labeo.services.file.FileService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.mail.MailService;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.NodeResourceImpl;
import net.labeo.services.templating.Template;
import net.labeo.services.templating.TemplateNotFoundException;
import net.labeo.services.templating.TemplatingService;
import net.labeo.services.webcore.FinderService;
import net.labeo.services.webcore.WebcoreService;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.Assembler;
import net.labeo.webcore.RunData;

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

/**
 * Provides skinning funcitonality.
 */
public class SkinServiceImpl
    extends BaseService
    implements SkinService
{
    // instance variables ////////////////////////////////////////////////////

    protected ResourceService resourceService;
    
    protected TemplatingService templatingService;
    
    protected StructureService structureService;
    
    protected FileService fileService;

    protected WebcoreService webcoreService;
    
    protected FinderService finderService;

    protected IntegrationService integrationService;

    protected MailService mailService;

    protected LoggingFacility log;
    
    protected String templateEncoding;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void init()
    {
        resourceService = (ResourceService)broker.
            getService(ResourceService.SERVICE_NAME);
        templatingService = (TemplatingService)broker.
            getService(TemplatingService.SERVICE_NAME);
        structureService = (StructureService)broker.
            getService(StructureService.SERVICE_NAME);
        fileService = (FileService)broker.
            getService(FileService.SERVICE_NAME);
        webcoreService = (WebcoreService)broker.
            getService(WebcoreService.SERVICE_NAME);
        finderService = (FinderService)broker.
            getService(FinderService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.
            getService(IntegrationService.SERVICE_NAME);
        mailService = (MailService)broker.
            getService(MailService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(LOGGING_FACILITY);
        templateEncoding = templatingService.getTemplateEncoding();
    }

    // public interface //////////////////////////////////////////////////////

    // skins /////////////////////////////////////////////////////////////////

    /**
     * Returns the currently selected skin for a site.
     */
    public String getCurrentSkin(SiteResource site)
        throws SkinException
    {
        try
        {
            NavigationNodeResource rootNode = structureService.
                getRootNode(site);
            return rootNode.getPreferences().get("site.skin").
                asString("default");
        }
        catch(Exception e)
        {
            throw new SkinException("failed to lookup site's root node", e);
        }
    }

    /**
     * Selects a skin for a site.
     */
    public void setCurrentSkin(SiteResource site, String skin)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        try
        {
            NavigationNodeResource rootNode = structureService.
                getRootNode(site);
            rootNode.getPreferences().
                set("site.skin", new Parameter(skin));
        }
        catch(Exception e)
        {
            throw new SkinException("failed to lookup site's root node", e);
        }
    }

    /**
     * Checks if the site has a skin with the given name.
     */
    public boolean hasSkin(SiteResource site, String skin)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if (res.length != 1)
        {
            throw new SkinException(
                "could not find skins node in site " + site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        return res.length == 1;
    }    

    /**
     * Returns a skin descriptor object.
     */
    public SkinResource getSkin(SiteResource site, String skin)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        return (SkinResource)res[0];
    }

    /**
     * Returns skins available for a given site.
     */
    public SkinResource[] getSkins(SiteResource site)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0]);
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
    public SkinResource createSkin(SiteResource site, String skin, SkinResource source,
        Subject subject)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        Resource skinsNode = res[0];
        res = resourceService.getStore().getResource(skinsNode, skin);
        if(res.length != 0)
        {
            throw new SkinException("skin "+skin+" already exists in site "+site.getName());
        }
        try
        {
            if(source == null)
            {
                SkinResource skinRes = SkinResourceImpl.createSkinResource(resourceService, skin, skinsNode, subject);
                NodeResourceImpl.createNodeResource(resourceService, "layouts", skinRes, subject);
                NodeResourceImpl.createNodeResource(resourceService, "components", skinRes, subject);
                NodeResourceImpl.createNodeResource(resourceService, "screens", skinRes, subject);
                fileService.mkdirs("/content/cms/sites/"+site.getName()+"/"+skin);
                fileService.mkdirs("/templates/cms/sites/"+site.getName()+"/"+skin);
                return skinRes;
            }
            else
            {
                resourceService.getStore().copyTree(source, skinsNode, skin, subject);
                res = resourceService.getStore().getResource(skinsNode, skin);
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
    public void renameSkin(SkinResource skin, String name)
        throws SkinException
    {
        SiteResource site = (SiteResource)skin.getParent().getParent();
        boolean current = getCurrentSkin(site).equals(skin.getName());
        if(hasSkin(site, name))
        {
            throw new SkinException("skin "+name+" already exists in site "+site.getName());
        }
        try
        {
            fileService.rename("/content/cms/sites/"+site.getName()+"/"+skin.getName(), 
                "/content/cms/sites/"+site.getName()+"/"+name);
            fileService.rename("/templates/cms/sites/"+site.getName()+"/"+skin.getName(), 
                "/templates/cms/sites/"+site.getName()+"/"+name);
            resourceService.getStore().setName(skin, name);
            if(current)
            {
                setCurrentSkin(site, name);
            } 
        }
        catch (IOException e)
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
    public void deleteSkin(SkinResource skin)
        throws SkinException
    {
        SiteResource site = (SiteResource)skin.getParent().getParent();
        if(getCurrentSkin(site).equals(skin.getName()))
        {
            throw new SkinException("cannon delete active skin");
        }
        try
        {
            resourceService.getStore().deleteTree(skin);
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
    public LayoutResource[] getLayouts(SiteResource site, String skin)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }        
        res = resourceService.getStore().getResource(res[0]);
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
    public boolean hasLayoutTemplate(SiteResource site, String skin, String name)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }   
        res = resourceService.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], name);
        return (res.length == 1);
    }

    /**
     * Returns a layout template provided by the skin.
     */
    public Template getLayoutTemplate(SiteResource site, String skin, String name)
        throws TemplateNotFoundException, SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }   
        res = resourceService.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }
        log.debug("trying to find layout resource: "+name+" in path: "+res[0].getPath());
        res = resourceService.getStore().getResource(res[0], name);
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
        return templatingService.getTemplate("cms", path);
    }

    // components ////////////////////////////////////////////////////////////

    /**
     * Returns visual variants available for a component.
     */
    public ComponentVariantResource[] getComponentVariants(SiteResource site, String skin,
                                                           String app, String component)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            // no components in this skin
            return new ComponentVariantResource[0];
        }
        res = resourceService.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            // no component variants for the application in this skin
            return new ComponentVariantResource[0];
        }
        res = resourceService.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            // no variants for the comonent in this skin
            return new ComponentVariantResource[0];
        }
        res = resourceService.getStore().getResource(res[0]);
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
    public ComponentVariantResource getComponentVariant(SiteResource site, String skin,
                                                           String app, String component, String variant)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            throw new SkinException("components nod in skin "+skin+" not present in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            throw new SkinException("no component variants for the application "+app+" in skin "+skin+" for site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            throw new SkinException("no component variants for the component "+component+" in application "+app+" in skin "+skin+" for site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            throw new SkinException("variant "+variant+" for the component "+component+" in application "+app+" in skin "+skin+" for site "+site.getName()+" is missing");
        }        
        return (ComponentVariantResource)res[0];                       
    }
                                 
    /**
     * Checks if the skin defines component variand with the given name.
     */
    public boolean hasComponentVariant(SiteResource site, String skin,
                                       String app, String component, 
                                       String variant)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = resourceService.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            return false;
        }
        res = resourceService.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            return false;
        }
        res = resourceService.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            return false;
        }
        res = resourceService.getStore().getResource(res[0], variant);
        return (res.length == 1);
    }

    /**
     * Creates a new variant of a component.
     */
    public ComponentVariantResource createComponentVariant(SiteResource site, 
        String skin, String app, String component, String variant, 
        Subject subject)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        Resource p = res[0];  
        res = resourceService.getStore().getResource(p, "components");
        try
        {
            if(res.length == 0)
            {
                p = NodeResourceImpl.createNodeResource(resourceService, "components", p, subject);
            }
            else
            {
                p = res[0];
            }
            res = resourceService.getStore().getResource(p, app);
            if(res.length == 0)
            {
                p = NodeResourceImpl.createNodeResource(resourceService, app, p, subject);
            }
            else
            {
                p = res[0];
            }
            res = resourceService.getStore().getResource(p, component);
            if(res.length == 0)
            {
                p = NodeResourceImpl.createNodeResource(resourceService, component, p, subject);
            }
            else
            {
                p = res[0];
            }
            res = resourceService.getStore().getResource(p, variant);
            if(res.length != 0)
            {
                throw new SkinException("variant "+variant+" already exists for component "+
                    component+" in skin "+skin+" for site "+site.getName());
            }
            else
            {
                return ComponentVariantResourceImpl.createComponentVariantResource(resourceService, variant, p, subject);
            }
        }
        catch(ValueRequiredException e)
        {
            throw new SkinException("unexpected exception", e);
        }
    }

    /**
     * Deletes a component variant;
     */
    public void deleteComponentVariant(SiteResource site, String skin, 
        String app, String component, String variant)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = resourceService.getStore().getResource(res[0], "components");
        if(res.length != 1)
        {
            throw new SkinException("could not find find components node in skin "+skin+
                                    " for site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " provides no variants for application "+app+
                                    " components");
        }
        res = resourceService.getStore().getResource(res[0], component);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " provides no variants for component "+
                                    component+" in application "+app);
        }
        res = resourceService.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " does not provide variant "+variant+
                                    " for component "+ component+
                                    " in application "+app);            
        }
        try
        {
            resourceService.getStore().deleteResource(res[0]);
        }
        catch (EntityInUseException e)
        {
            throw new SkinException("unexpected exception", e);
        }
    }
     
    /**
     * Return a component variant template provided by the skin.
     */
    public Template getComponentTemplate(SiteResource site, String skin,
                                         String app, String component, 
                                         String variant, String state)
        throws TemplateNotFoundException, SkinException
    {
        String path = getComponentTemplatePath(site, skin, 
                                               app, component, 
                                               variant, state, true);
        return templatingService.getTemplate("cms", path);
    }

    /**
     * Checks if a screen template provided by the skin.
     */
    public boolean hasComponentTemplate(SiteResource site, String skin,
                                     String app, String component, 
                                     String variant, String state)
        throws SkinException
    {
        String path = getComponentTemplatePath(site, skin, 
                                               app, component, 
                                               variant, state, false);
        if(path != null)
        {
            return templatingService.templateExists("cms", path);
        }
        else
        {
            return false;
        }
    }

    protected void invalidateComponentTemplate(SiteResource site, String skin,
        String app, String component, String variant, String state)
        throws SkinException
    {
        String path = getComponentTemplatePath(site, skin, 
            app, component, 
            variant, state, true);

        templatingService.invalidateTemplate("cms", path);
    }
    
    /**
     * Returns finder path of a component.
     */
    public String getComponentTemplatePath(SiteResource site, String skin,
                                           String app, String component, 
                                           String variant, String state, 
                                           boolean critical)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = resourceService.getStore().getResource(res[0], "components");
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
        res = resourceService.getStore().getResource(res[0], app);
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
        res = resourceService.getStore().getResource(res[0], component);
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
        res = resourceService.getStore().getResource(res[0], variant);
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

        ComponentResource integComp = integrationService.getComponent(app, component);
        if(integComp == null)
        {
            throw new SkinException("application "+app+" does not provide component"+
                                    component);
        }
        String integState = StringUtils.
                    foldCase(StringUtils.FOLD_LOWER_FIRST_UNDERSCORES, state);
        if(!((state.equalsIgnoreCase("Default") 
              && integrationService.getComponentStates(integComp).length == 0) 
             || integrationService.hasState(integComp, integState)))
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
    public ScreenVariantResource[] getScreenVariants(SiteResource site, String skin,
                                                           String app, String screen)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            // no screens in this skin
            return new ScreenVariantResource[0];
        }
        res = resourceService.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            // no screen variants for the application in this skin
            return new ScreenVariantResource[0];
        }
        res = resourceService.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            // no variants for the comonent in this skin
            return new ScreenVariantResource[0];
        }
        res = resourceService.getStore().getResource(res[0]);
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
    public boolean hasScreenVariant(SiteResource site, String skin,
                                       String app, String screen, 
                                       String variant)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = resourceService.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            return false;
        }
        res = resourceService.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            return false;
        }
        res = resourceService.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            return false;
        }
        res = resourceService.getStore().getResource(res[0], variant);
        return (res.length == 1);
    }
     
    /**
     * Returns visual variant of a screen.
     */
    public ScreenVariantResource getScreenVariant(SiteResource site, String skin,
                                                           String app, String screen, String variant)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            throw new SkinException("screens nod in skin "+skin+" not present in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            throw new SkinException("no screen variants for the application "+app+" in skin "+skin+" for site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            throw new SkinException("no screen variants for the screen "+screen+" in application "+app+" in skin "+skin+" for site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            throw new SkinException("variant "+variant+" for the screen "+screen+" in application "+app+" in skin "+skin+" for site "+site.getName()+" is missing");
        }        
        return (ScreenVariantResource)res[0];                       
    }
     
    /**
     * Return a screen variant template provided by the skin.
     */
    public Template getScreenTemplate(SiteResource site, String skin,
                                      String app, String screen, 
                                      String variant, String state)
        throws TemplateNotFoundException, SkinException
    {
        String path = getScreenTemplatePath(site, skin, app, screen,
                                            variant, state, true);
        return templatingService.getTemplate("cms", path);
    }


    /**
     * Checks if a screen template provided by the skin.
     */
    public boolean hasScreenTemplate(SiteResource site, String skin,
                                     String app, String screen, 
                                     String variant, String state)
        throws SkinException
    {
        String path = getScreenTemplatePath(site, skin, app, screen,
                                            variant, state, false);
        if(path != null)
        {
            return templatingService.templateExists("cms", path);
        }
        else
        {
            return true;
        }
    }

    protected void invalidateScreenTemplate(SiteResource site, String skin, String app, String screen,
        String variant, String state) throws SkinException
    {
        String path = getScreenTemplatePath(site, skin, app, screen, variant, state, true);
        templatingService.invalidateTemplate("cms", path);
    }
    
    /**
     * Returns finder path of a screen.
     */
    public String getScreenTemplatePath(SiteResource site, String skin,
                                        String app, String screen, 
                                        String variant, String state, 
                                        boolean critical)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = resourceService.getStore().getResource(res[0], "screens");
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
        res = resourceService.getStore().getResource(res[0], app);
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
        res = resourceService.getStore().getResource(res[0], screen);
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
        res = resourceService.getStore().getResource(res[0], variant);
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

        ScreenResource integScreen = integrationService.getScreen(app, screen);
        if(integScreen == null)
        {
            throw new SkinException("application "+app+" does not provide screen"+
                                    screen);
        }
        if(!((state.equalsIgnoreCase("Default") 
              && integrationService.getScreenStates(integScreen).length == 0) 
             || integrationService.hasState(integScreen, state)))
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
        return fileService.exists(filePath);
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
	public void createLayoutTemplate(SiteResource site, String skin, String layout, 
		String contents, Subject subject)
		throws SkinException
	{
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }   
        res = resourceService.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }
        log.debug("trying to find layout resource: "+layout+" in path: "+res[0].getPath());
        Resource parent = res[0];
        res = resourceService.getStore().getResource(parent, layout);
        if(res.length > 0)
        {
            throw new SkinException("layout "+layout+" already exists in skin "+
                skin+" for site "+site.getName());
        }
		String path = getLayoutTemplatePath(site, skin, layout);
		if(fileService.exists(path))
		{
            throw new SkinException("refusing to overwrite "+path);
		}
        try
        {
            LayoutResourceImpl.createLayoutResource(resourceService, layout, parent, subject);
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
	public void deleteLayoutTemplate(SiteResource site, String skin, String layout)
		throws SkinException
	{
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }   
        res = resourceService.getStore().getResource(res[0], "layouts");
        if(res.length != 1)
        {
            throw new SkinException("could not find find layouts node in skin "+skin+
                                    " for site "+site.getName());
        }
        log.debug("trying to find layout resource: "+layout+" in path: "+res[0].getPath());
        res = resourceService.getStore().getResource(res[0], layout);
        if(res.length == 0)
        {
            throw new SkinException("layout "+layout+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            resourceService.getStore().deleteResource(res[0]);
        }
        catch(Exception e)
        {
            throw new SkinException("failed to delete layout resource", e);
        }
		String path = getLayoutTemplatePath(site, skin, layout);
		if(!fileService.exists(path))
		{
			throw new SkinException(path+" does not exist");
		}
		try
		{
			fileService.delete(path);
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
		if(!fileService.exists(path))
		{
			throw new SkinException("layout "+layout+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			return fileService.read(path, templateEncoding);
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
        if(!fileService.exists(path))
        {
            throw new SkinException("layout "+layout+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileService.read(path, out);
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
        if(!fileService.exists(path))
        {
            throw new SkinException("layout "+layout+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        else
        {
            return fileService.length(path);
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
		if(!fileService.exists(path))
		{
			throw new SkinException("layout "+layout+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
        writeTemplate(path, contents, "failed to modify layout template contents");
        invalidateLayoutTemplate(site, skin, layout);
	}

	// components

	public void createComponentTemplate(SiteResource site, String skin, 
		String app, String component, String variant, String state, 
        String contents)
		throws SkinException
	{
		String path = getComponentTemplatePath(site, skin, app, component, 
			variant, state);
		if(fileService.exists(path))
		{
			throw new SkinException("component "+app+":"+component+" variant "+
				variant+" already exists in skin "+
				skin+" for site "+site.getName());
		}
        writeTemplate(path, contents, "failed to create layout");
        invalidateComponentTemplate(site, skin, app, component, variant, state);
	}
    	
	public void deleteComponentTemplate(SiteResource site, String skin, 
		String app,	String component, String variant, String state)
		throws SkinException
	{
		String path = getComponentTemplatePath(site, skin, app, component,
			variant, state);
		if(!fileService.exists(path))
		{
			throw new SkinException("component "+app+":"+component+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			fileService.delete(path);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to delete layout", e);
		}				
        invalidateComponentTemplate(site, skin, app, component, variant, state);
	}
    
	public String getComponentTemplateContents(SiteResource site, String skin, 
		String app,  String component, String variant, String state)
		throws SkinException
	{
		String path = getComponentTemplatePath(site, skin, app, component,
			variant, state);
		if(!fileService.exists(path))
		{
			throw new SkinException("component "+app+":"+component+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			return fileService.read(path, templateEncoding);
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
        if(!fileService.exists(path))
        {
            throw new SkinException("component "+app+":"+component+" variant "+
                variant+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileService.read(path, out);
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
            if(fileService.exists("/templates/"+app+"/"+
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
            return fileService.read(path, templateEncoding);
        }
        catch(Exception e)
        {
            throw new SkinException("failed to read template contents", e);
        }
    }
    	
	public void setComponentTemplateContents(SiteResource site, String skin, 
		String app, String component, String variant, String state, 
        String contents)
		throws SkinException
	{
		String path = getComponentTemplatePath(site, skin, app, component,
			variant, state);
		if(!fileService.exists(path))
		{
			throw new SkinException("component "+app+":"+component+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
        
        writeTemplate(path, contents, "failed to modify layout template contents");
        invalidateComponentTemplate(site, skin, app, component, variant, state);
	}

    public long getComponentTemplateLength(SiteResource site, String skin, 
        String app,  String component, String variant, String state)
        throws SkinException
    {
        String path = getComponentTemplatePath(site, skin, app, component,
            variant, state);
        if(!fileService.exists(path))
        {
            throw new SkinException("component "+app+":"+component+" variant "+
                variant+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        return fileService.length(path);
    }

	// screens

    /**
     * Creates a new variant of a screen.
     */
    public ScreenVariantResource createScreenVariant(SiteResource site, 
        String skin, String app, String screen, String variant, 
        Subject subject)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }
        Resource p = res[0];  
        res = resourceService.getStore().getResource(p, "screens");
        try
        {
            if(res.length == 0)
            {
                p = NodeResourceImpl.createNodeResource(resourceService, "screens", p, subject);
            }
            else
            {
                p = res[0];
            }
            res = resourceService.getStore().getResource(p, app);
            if(res.length == 0)
            {
                p = NodeResourceImpl.createNodeResource(resourceService, app, p, subject);
            }
            else
            {
                p = res[0];
            }
            res = resourceService.getStore().getResource(p, screen);
            if(res.length == 0)
            {
                p = NodeResourceImpl.createNodeResource(resourceService, screen, p, subject);
            }
            else
            {
                p = res[0];
            }
            res = resourceService.getStore().getResource(p, variant);
            if(res.length != 0)
            {
                throw new SkinException("variant "+variant+" already exists for screen "+
                    screen+" in skin "+skin+" for site "+site.getName());
            }
            else
            {
                return ScreenVariantResourceImpl.createScreenVariantResource(resourceService, variant, p, subject);
            }
        }
        catch(ValueRequiredException e)
        {
            throw new SkinException("unexpected exception", e);
        }
    }

   /**
     * Deletes a screen variant;
     */
    public void deleteScreenVariant(SiteResource site, String skin, 
        String app, String screen, String variant)
        throws SkinException
    {
        Resource[] res = resourceService.getStore().getResource(site, "skins");
        if(res.length != 1)
        {
            throw new SkinException("could not find skins node in site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], skin);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" not present in site "+site.getName());
        }  
        res = resourceService.getStore().getResource(res[0], "screens");
        if(res.length != 1)
        {
            throw new SkinException("could not find find screens node in skin "+skin+
                                    " for site "+site.getName());
        }
        res = resourceService.getStore().getResource(res[0], app);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " provides no variants for application "+app+
                                    " screens");
        }
        res = resourceService.getStore().getResource(res[0], screen);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " provides no variants for screen "+
                                    screen+" in application "+app);
        }
        res = resourceService.getStore().getResource(res[0], variant);
        if(res.length != 1)
        {
            throw new SkinException("skin "+skin+" in site "+site.getName()+
                                    " does not provide variant "+variant+
                                    " for screen "+ screen+
                                    " in application "+app);            
        }
        try
        {
            resourceService.getStore().deleteResource(res[0]);
        }
        catch (EntityInUseException e)
        {
            throw new SkinException("unexpected exception", e);
        }
    }

	public void createScreenTemplate(SiteResource site, String skin, 
		String app, String screen, String variant, String state, String contents)
		throws SkinException
	{
		String path = getScreenTemplatePath(site, skin, app, screen, 
			variant, state);
		if(fileService.exists(path))
		{
			throw new SkinException("screen "+app+":"+screen+" variant "+
				variant+" already exists in skin "+
				skin+" for site "+site.getName());
		}
        
        writeTemplate(path, contents, "failed to create layout");
        invalidateScreenTemplate(site, skin, app, screen, variant, state);	
    }
    	
	public void deleteScreenTemplate(SiteResource site, String skin, 
		String app,	String screen, String variant, String state)
		throws SkinException
	{
		String path = getScreenTemplatePath(site, skin, app, screen,
			variant, state);
		if(!fileService.exists(path))
		{
			throw new SkinException("screen "+app+":"+screen+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			fileService.delete(path);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to delete layout", e);
		}				
        invalidateScreenTemplate(site, skin, app, screen, variant, state);  
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
            if(fileService.exists("/templates/"+app+"/"+
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
            return fileService.read(path, templateEncoding);
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
        if(!fileService.exists(path))
        {
            throw new SkinException("screen "+app+":"+screen+" variant "+
                variant+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        return fileService.length(path);
    }

    public void getScreenTemplateContents(SiteResource site, String skin, 
        String app,  String screen, String variant, String state,
        OutputStream out)
        throws SkinException
    {
        String path = getScreenTemplatePath(site, skin, app, screen,
            variant, state);
        if(!fileService.exists(path))
        {
            throw new SkinException("screen "+app+":"+screen+" variant "+
                variant+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileService.read(path, out);
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
		if(!fileService.exists(path))
		{
			throw new SkinException("screen "+app+":"+screen+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
		try
		{
			return fileService.read(path, templateEncoding);
		}
		catch(IOException e)
		{
			throw new SkinException("failed to retrieve layout template contents", e);
		}		
	}
    	
	public void setScreenTemplateContents(SiteResource site, String skin, 
		String app, String screen, String variant, String state, String contents)
		throws SkinException
	{
		String path = getScreenTemplatePath(site, skin, app, screen,
			variant, state);
		if(!fileService.exists(path))
		{
			throw new SkinException("screen "+app+":"+screen+" variant "+
				variant+" does not exist in skin "+
				skin+" for site "+site.getName());
		}
        
        writeTemplate(path, contents, "failed to modify layout template contents");
        invalidateScreenTemplate(site, skin, app, screen, variant, state);  
	}

    // static content
    
    /**
     * Return static conent file MIME type.
     *
     * <p>This implementation guesses the type by file extension using 
     * {@see MailService#getContentType(String)}.</p>
     * 
     * @param site the site.
     * @param skin the skin.
     * @param path the content file path.
     */
    public String getContentFileType(SiteResource site, String skin, String path)
    {
        return mailService.getContentType(path);
    }
    
    public long getContentFileLength(SiteResource site, String skin, String path)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(fileService.exists(filePath))
        {
            return fileService.length(filePath);        
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
            fileService.write(filePath, data);
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
        if(!fileService.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileService.read(filePath, out);
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
        if(!fileService.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            return fileService.read(filePath, encoding);
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
        if(!fileService.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileService.write(filePath, in);
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
        if(!fileService.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileService.write(filePath, contents, encoding);
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
        if(!fileService.exists(filePath))
        {
            throw new SkinException("file "+path+" does not exist in skin "+
                skin+" for site "+site.getName());
        }
        try
        {
            fileService.delete(filePath);
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
        if(fileService.exists(filePath))
        {
            throw new SkinException("directory "+path+" already exists");
        }
        try
        {
            fileService.mkdirs(filePath);
        }
        catch(IOException e)
        {
            throw new SkinException("failed to create directory", e);
        }                       
    }
        
    public void deleteContentDirectory(SiteResource site, String skin, 
        String path)
        throws SkinException
    {
        String filePath = getContentPath(site, skin, path);        
        if(!fileService.exists(filePath))
        {
            throw new SkinException("directory "+path+
                " does not exist in skin "+skin+" for site "+site.getName());
        }
        try
        {
            fileService.delete(filePath);
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
        templatingService.invalidateTemplate("cms", path);
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
        if(!fileService.exists(dir))
        {
            throw new SkinException(dir+" does not exist");
        }
        String[] children = fileService.list(dir);
        if(children == null)
        {
        	return null;
        }
        ArrayList temp = new ArrayList(children.length);
        for(int i=0; i<children.length; i++)
        {
            if(fileService.isDirectory(dir+"/"+children[i]) == directories)
            {
                temp.add(children[i]);
            }
        }
        String[] result = new String[temp.size()];
        temp.toArray(result);
        return result;
    }
    
    protected void copyDir(String src, String dst)
        throws IOException
    {
        if(!fileService.exists(src))
        {
            throw new IOException("source directory "+src+" does not exist");
        }
        if(!fileService.canRead(src))
        {
            throw new IOException("source directory "+src+" is not readable");
        }
        if(!fileService.isDirectory(src))
        {
            throw new IOException(src+" is not a directory");
        }
        fileService.mkdirs(dst);
        String[] srcFiles = fileService.list(src);
        for(int i=0; i<srcFiles.length; i++)
        {
            String name = srcFiles[i];
            if(name.startsWith(".") || name.equals("CVS"))
            {
                continue;
            }
            if(fileService.isDirectory(src+"/"+name))
            {
                copyDir(src+"/"+name, dst+"/"+name);
            }
            else
            {
                fileService.copyFile(src+"/"+name, dst+"/"+name);
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
            if(fileService.isDirectory(path))
            {
                String[] children = fileService.list(path);
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
            fileService.delete(path);
        }
    }
    
    private void writeTemplate(String path, String contents, String message)
        throws SkinException
    {
        try
        {
            HTMLEntityEncoder encoder = new HTMLEntityEncoder();
            if(!fileService.exists(path))
            {
                fileService.mkdirs(StringUtils.directoryPath(path));
            }
            String encoded = contents.length() > 0 ?
                encoder.encodeHTML(contents, templateEncoding) :
                contents;
            fileService.write(path, encoded, templateEncoding);
        }
        catch(IOException e)
        {
            throw new SkinException(message, e);
        }
    }
}

