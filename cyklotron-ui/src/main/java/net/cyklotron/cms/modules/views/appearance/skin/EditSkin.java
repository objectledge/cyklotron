package net.cyklotron.cms.modules.views.appearance.skin;

import java.util.HashSet;
import java.util.Set;

import net.labeo.Labeo;
import net.labeo.services.table.*;
import net.labeo.services.table.PathTreeTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.ComponentStateResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.integration.ScreenStateResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.skins.LayoutResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.SkinResource;

public class EditSkin
    extends BaseAppearanceScreen
{
    protected TableService tableService;

    protected IntegrationService integrationService;

    public EditSkin()
    {
        tableService = (TableService)Labeo.getBroker().
            getService(TableService.SERVICE_NAME);
        integrationService = (IntegrationService)Labeo.getBroker().
            getService(IntegrationService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            String skinName = parameters.get("skin");
            SkinResource skin = skinService.getSkin(site, skinName);
            templatingContext.put("skin", skin);
            templatingContext.put("current_skin", skinService.getCurrentSkin(site));
            TableState state = tableService.getLocalState(data, "screens:cms:appearance,EditSkin:"+
                                                     site.getName()+":"+skinName);
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_TREE);
                state.setRootId("0");
                state.setShowRoot(true);
                state.setExpanded("0");
                state.setPageSize(0);
                state.setMultiSelect(false);
                state.setSortColumnName("element");
            }
            TableColumn[] cols = new TableColumn[1];
            cols[0] = new TableColumn("element", PathTreeElement.getComparator("name", i18nContext.getLocale()()));
            PathTreeTableModel model = new PathTreeTableModel(cols);
            model.bind("/", new PathTreeElement("skin", "label"));
            model.bind("/templates", new PathTreeElement("templates", "label"));
            model.bind("/templates/layouts", new PathTreeElement("layouts", "label"));
            LayoutResource[] layouts = skinService.getLayouts(site, skinName);
            Set definedLayouts = new HashSet();
            for(int i=0; i<layouts.length; i++)
            {
                definedLayouts.add(layouts[i].getName());
            }
            net.cyklotron.cms.style.LayoutResource[] styleLayouts =
                styleService.getLayouts(site);
            for(int i=0; i<styleLayouts.length; i++)
            {
                PathTreeElement elm = new PathTreeElement(styleLayouts[i].getName(), "layout");
                if(definedLayouts.contains(styleLayouts[i].getName()))
                {
                    elm.set("present", "true");
                }
                model.bind("/templates/layouts/"+styleLayouts[i].getName(), elm);
            }
            ApplicationResource[] applications = integrationService.getApplications();
            if(applications.length > 0)
            {
                model.bind("/templates/applications", new PathTreeElement("applications", "label"));
                for(int i=0; i<applications.length; i++)
                {
                    if(applications[i].getEnabled())
                    {
                        ComponentResource[] components = integrationService.
                            getComponents(applications[i]);
                        ScreenResource[] screens = integrationService.
                            getScreens(applications[i]);

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
                                        getComponentStates(components[j]);

                                    ComponentVariantResource[] variants = skinService.
                                        getComponentVariants(site, skinName,
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
                                            elm.set("present", skinService.hasComponentTemplate(
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
                                                        skinService.hasComponentTemplate(
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
                                        getScreenStates(screens[j]);

                                    ScreenVariantResource[] variants = skinService.
                                        getScreenVariants(site, skinName,
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
                                                    skinService.hasScreenTemplate(
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
                                                        skinService.hasScreenTemplate(
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
            templatingContext.put("table", new TableTool(state, model, null));
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
