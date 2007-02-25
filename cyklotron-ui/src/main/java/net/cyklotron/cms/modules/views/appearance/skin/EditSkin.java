package net.cyklotron.cms.modules.views.appearance.skin;

import java.util.HashSet;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.PathTreeElement;
import org.objectledge.table.generic.PathTreeTableModel;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.ComponentStateResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.integration.ScreenStateResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.skins.LayoutResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.SkinResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

public class EditSkin
    extends BaseAppearanceScreen
{
    public EditSkin(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            String skinName = parameters.get("skin");
            SkinResource skin = skinService.getSkin(coralSession, site, skinName);
            templatingContext.put("skin", skin);
            templatingContext.put("current_skin", skinService.getCurrentSkin(coralSession, site));
            TableState state = tableStateManager.getState(context, "screens:cms:appearance,EditSkin:"+
                                                     site.getName()+":"+skinName);
            if(state.isNew())
            {
                String rootId = Integer.toString("/".hashCode());
                state.setTreeView(true);
                state.setRootId(rootId);
                state.setShowRoot(true);
                state.setExpanded(rootId);
                state.setPageSize(0);
                state.setSortColumnName("element");
            }
            TableColumn[] cols = new TableColumn[1];
            cols[0] = new TableColumn("element", PathTreeElement.getComparator("name", i18nContext.getLocale()));
            PathTreeTableModel model = new PathTreeTableModel(cols);
            model.bind("/", new PathTreeElement("skin", "label"));
            model.bind("/templates", new PathTreeElement("templates", "label"));
            model.bind("/templates/layouts", new PathTreeElement("layouts", "label"));
            LayoutResource[] layouts = skinService.getLayouts(coralSession, site, skinName);
            Set definedLayouts = new HashSet();
            for(int i=0; i<layouts.length; i++)
            {
                definedLayouts.add(layouts[i].getName());
            }
            net.cyklotron.cms.style.LayoutResource[] styleLayouts =
                styleService.getLayouts(coralSession, site);
            for(int i=0; i<styleLayouts.length; i++)
            {
                PathTreeElement elm = new PathTreeElement(styleLayouts[i].getName(), "layout");
                if(definedLayouts.contains(styleLayouts[i].getName()))
                {
                    elm.set("present", "true");
                }
                model.bind("/templates/layouts/"+styleLayouts[i].getName(), elm);
            }
            ApplicationResource[] applications = integrationService.getApplications(coralSession);
            if(applications.length > 0)
            {
                model.bind("/templates/applications", new PathTreeElement("applications", "label"));
                for(int i=0; i<applications.length; i++)
                {
                    if(integrationService.isApplicationEnabled(coralSession, site, applications[i]))
                    {
                        ComponentResource[] components = integrationService.
                            getComponents(coralSession, applications[i]);
                        ScreenResource[] screens = integrationService.
                            getScreens(coralSession, applications[i]);

                        if(components.length + screens.length > 0)
                        {
                            PathTreeElement elm = new PathTreeElement(applications[i].getName(),
                                                              "application");
                            model.bind("/templates/applications/"+applications[i].getName(), elm);

                            if(components.length > 0)
                            {
                                elm = new PathTreeElement("components", "label");
                                model.bind("/templates/applications/"+
                                           applications[i].getName()+"/components", elm);

                                for(int j=0; j<components.length; j++)
                                {
                                    elm = new PathTreeElement(components[j].getName(), "component");
                                    elm.set("app", applications[i].getName());
                                    model.bind("/templates/applications/"+
                                               applications[i].getName()+
                                               "/components/"+ components[j].getName(), elm);

                                    ComponentStateResource[] states = integrationService.
                                        getComponentStates(coralSession, components[j]);

                                    ComponentVariantResource[] variants = skinService.
                                        getComponentVariants(coralSession, site, skinName,
                                                             applications[i].getApplicationName(),
                                                             components[j].getComponentName());
                                    if(states.length == 0)
                                    {
                                        for(int k=0; k<variants.length; k++)
                                        {
                                            elm = new PathTreeElement(variants[k].getName(),
                                                                  "stateless_component_variant");
                                            elm.set("app", applications[i].getName());
                                            elm.set("component", components[j].getName());
                                            elm.set("present", skinService.hasComponentTemplate(coralSession, 
                                                        site, skin.getName(),
                                                        components[j].getApplicationName(),
                                                        components[j].getComponentName(),
                                                        variants[k].getName(), "default") ?
                                                    "true" : "false");
                                            model.bind("/templates/applications/"+
                                                       applications[i].getName()+
                                                       "/components/"+components[j].getName()+
                                                       "/"+variants[k].getName(), elm);
                                        }
                                    }
                                    else
                                    {
                                        for(int k=0; k<variants.length; k++)
                                        {
                                            elm = new PathTreeElement(variants[k].getName(),
                                                                  "stateful_component_variant");
                                            elm.set("app", applications[i].getName());
                                            elm.set("component", components[j].getName());
                                            elm.set("variant", variants[k].getName());
                                            model.bind("/templates/applications/"+
                                                       applications[i].getName()+
                                                       "/components/"+components[j].getName()+
                                                       "/"+variants[k].getName(), elm);
                                            for(int l=0; l<states.length; l++)
                                            {
                                                elm = new PathTreeElement(states[l].getName(),
                                                                      "component_state");
                                                elm.set("app", applications[i].getName());
                                                elm.set("component", components[j].getName());
                                                elm.set("variant", variants[k].getName());
                                                elm.set("present",
                                                        skinService.hasComponentTemplate(coralSession, 
                                                            site, skin.getName(),
                                                            components[j].getApplicationName(),
                                                            components[j].getComponentName(),
                                                            variants[k].getName(),
                                                            states[l].getName()) ?
                                                        "true" : "false");
                                                model.bind("/templates/applications/"+
                                                           applications[i].getName()+
                                                           "/components/"+components[j].getName()+
                                                           "/"+variants[k].getName()+"/"+
                                                           states[l].getName(), elm);
                                            }
                                        }
                                    }
                                }
                            }

                            if(screens.length > 0)
                            {
                                elm = new PathTreeElement( "screens", "label");
                                model.bind("/templates/applications/"+applications[i].getName()+
                                           "/screens", elm);

                                for(int j=0; j<screens.length; j++)
                                {
                                    elm = new PathTreeElement(screens[j].getName(), "screen");
                                    elm.set("app", applications[i].getName());
                                    model.bind("/templates/applications/"+
                                               applications[i].getName()+
                                               "/screens/"+screens[j].getName(),
                                               elm);

                                    ScreenStateResource[] states = integrationService.
                                        getScreenStates(coralSession, screens[j]);

                                    ScreenVariantResource[] variants = skinService.
                                        getScreenVariants(coralSession, site, skinName,
                                                          applications[i].getApplicationName(),
                                                          screens[j].getScreenName());
                                    if(states.length == 0)
                                    {
                                        for(int k=0; k<variants.length; k++)
                                        {
                                            elm = new PathTreeElement(variants[k].getName(),
                                                                  "stateless_screen_variant");
                                            elm.set("app", applications[i].getName());
                                            elm.set("screen", screens[j].getName());
                                            elm.set("variant", variants[k].getName());
                                            elm.set("present",
                                                    skinService.hasScreenTemplate(coralSession, 
                                                        site, skin.getName(),
                                                        screens[j].getApplicationName(),
                                                        screens[j].getScreenName(),
                                                        variants[k].getName(), "default") ?
                                                    "true" : "false");
                                            model.bind("/templates/applications/"+
                                                       applications[i].getName()+
                                                       "/screens/"+screens[j].getName()+
                                                       "/"+variants[k].getName(), elm);
                                        }
                                    }
                                    else
                                    {
                                        for(int k=0; k<variants.length; k++)
                                        {
                                            elm = new PathTreeElement(variants[k].getName(),
                                                                  "stateful_screen_variant");
                                            elm.set("app", applications[i].getName());
                                            elm.set("screen", screens[j].getName());
                                            model.bind("/templates/applications/"+
                                                       applications[i].getName()+
                                                       "/screens/"+screens[j].getName()+
                                                       "/"+variants[k].getName(), elm);
                                            for(int l=0; l<states.length; l++)
                                            {
                                                elm = new PathTreeElement(states[l].getName(),
                                                                      "screen_state");
                                                elm.set("app", applications[i].getName());
                                                elm.set("screen", screens[j].getName());
                                                elm.set("variant", variants[k].getName());
                                                elm.set("present",
                                                        skinService.hasScreenTemplate(coralSession, 
                                                            site, skin.getName(),
                                                            screens[j].getApplicationName(),
                                                            screens[j].getScreenName(),
                                                            variants[k].getName(),
                                                            states[l].getName()) ?
                                                        "true" : "false");
                                                model.bind("/templates/applications/"+
                                                           applications[i].getName()+
                                                           "/screens/"+screens[j].getName()+
                                                           "/"+variants[k].getName()+"/"+
                                                           states[l].getName(), elm);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            PathTreeElement elm = new PathTreeElement("content", "directory");
            elm.set("top", "true");
            elm.set("path", ",");
            model.bind("/content", elm);
            bindContent(site, skinName, model, "");

            model.bind("/stylesheet", new PathTreeElement("stylesheet", "stylesheet"));
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load skin information", e);
        }
    }

    protected void bindContent(SiteResource site, String skinName,
                               PathTreeTableModel model, String path)
        throws Exception
    {
        String[] dirs = skinService.getContentDirectoryNames(site, skinName, path);
        for(int i=0; i<dirs.length; i++)
        {
            PathTreeElement elm = new PathTreeElement(dirs[i], "directory");
            elm.set("path", path.concat(dirs[i]).concat("/").replace('/',','));
            model.bind("/content/"+path+dirs[i], elm);
            bindContent(site, skinName, model, path+dirs[i]+"/");
        }
        String[] files = skinService.getContentFileNames(site, skinName, path);
        for(int i=0; i<files.length; i++)
        {
            if(path.equals("") && files[i].equals("style.css"))
            {
                continue;
            }
            PathTreeElement elm = new PathTreeElement(files[i], "file");
            elm.set("path", path.concat(files[i]).replace('/',','));
            elm.set("content_type", skinService.getContentFileType(site, skinName, path+files[i]));
            model.bind("/content/"+path+files[i], elm);
        }
    }
}
