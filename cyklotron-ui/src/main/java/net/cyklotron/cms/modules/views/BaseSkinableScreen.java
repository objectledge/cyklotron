package net.cyklotron.cms.modules.views;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.components.EmbeddedScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleService;
import net.labeo.services.templating.Context;
import net.labeo.services.templating.Template;
import net.labeo.services.webcore.FinderService;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.Assembler;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * The main screen of the CMS application that displays page contents.
 */
public class BaseSkinableScreen
    extends BaseCMSScreen
{
    protected StructureService structureService;

    protected StyleService styleService;

    protected SkinService skinService;

    protected PreferencesService preferencesService;

    private Map methodMap = new HashMap();

    protected FinderService finderService;

    public BaseSkinableScreen()
    {
        super();
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
        styleService = (StyleService)broker.getService(StyleService.SERVICE_NAME);
        skinService = (SkinService)broker.getService(SkinService.SERVICE_NAME);
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
        finderService = (FinderService)broker.getService(FinderService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(context.containsKey("stackTrace"))
        {
            return;
        }
        if(isNodeDefined())
        {
            setupCMSLayout(data, getNode());
        }
        prepareState(data, context);
    }

    public void setupCMSLayout(RunData data, NavigationNodeResource node)
        throws ProcessingException
    {
        try
        {
            if(node != null)
            {
                CmsData cmsData = cmsDataFactory.getCmsData(context);
                
                // choose layout normally
                StyleResource style = node.getEffectiveStyle();
                int level = node.getLevel();
                String layout = styleService.getLayout(style, level);

                Template layoutTemplate = skinService.
                    getLayoutTemplate(node.getSite(), cmsData.getSkinName(), layout);
                data.setLayoutTemplate(layoutTemplate);

                // fall back onto emergency layout
                if(cmsData.getBrowseMode().equals("emergency"))
                {
                    data.setLayoutTemplate("Emergency");
                }
            }
            else
            {
                mvcContext.setView(structureService.getInvalidNodeErrorScreen());
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to setup CMS layout", e);
        }
    }

    public boolean requiresLogin(RunData data)
        throws ProcessingException
    {
        if(data.getContext().containsKey("stackTrace"))
        {
            return false;
        }
        if(isNodeDefined())
        {
            return !getNode().canView(coralSession.getUserSubject(), new Date());
        }
        else
        {
            // not sure about this one...
            return true;
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        if(data.getContext().containsKey("stackTrace"))
        {
            return true;
        }
        if(isNodeDefined())
        {
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            return getNode().canView(cmsData, cmsData.getUserData().getSubject());
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns a components template.
     *
     * TODO: Rethink the way the errors are being handled, especially when a skinnable screen is
     *       executed outside of the site
     *
     * @param data the RunData
     * @return a template to be used for rendering this block.
     */
    public Template getTemplate(RunData data)
        throws NotFoundException
    {
        CmsData cmsData;
        try
        {
            cmsData = cmsDataFactory.getCmsData(context);
        }
        catch(ProcessingException e)
        {
            // outside of cms scope?
            log.warning("faied to acquire CmsData",e );
            return super.getTemplate(data);
        }

        // get site
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
            if(site == null)
            {            
                return super.getTemplate(data);
            }
        }

        // 1. get skin name
        String skin = cmsData.getSkinName();

        // 2. get screen info
        String app = null;
        String screen = null;
        String variant = null;
        String state;
        try
        {
            state = getState(data);
            Parameters config = cmsData.getPreferences();
            app = config.get("screen.app",null);
            screen = config.get("screen.class",null);
            if(app != null && screen != null)
            {
                variant = config.get("screen.variant."+app+"."+
                    screen.replace(',','.'),"Default");
            }
        }
        catch(ProcessingException e)
        {
            throw new NotFoundException("failed to determine state", e);
        }
        if(app == null || screen == null)
        {
            log.warning(getClass().getName()+" is dervied from BaseSkinableScreen "+
                        "but was launched outside of EmbeddedScreen component");
            return super.getTemplate(data);
        }

        log.debug("BaseSkinnableScreen "+app+":"+screen+":"+variant+":"+state);

        // 3. get template object
        Template templ = null;

        try
        {
            // if skin defines a template for the variant
            if(skinService.hasScreenVariant(site, skin, app, screen, variant))
            {
                templ = skinService.getScreenTemplate(site, skin, app, screen, variant, state);
            }
            else
            {
                templ = getAppScreenTemplate(data, app, screen, state);
            }
        }
        catch(Exception e)
        {
            log.error("failed to lookup template for screen "+
                      app+":"+screen+" site "+site.getName()+" skin "+skin+
                      " variant "+variant+" state "+state, e);
            templ = null;
        }

        // this one throws an exception - we cannot generate component's UI without any templates.
        if(templ == null)
        {
            templ = super.getTemplate(data);
        }

        return templ;
    }

    protected Template getAppScreenTemplate(RunData data, String app, String component, String state)
        throws NotFoundException
    {
        if(!state.equalsIgnoreCase("Default"))
        {
            component = component + StringUtils.
                foldCase(StringUtils.FOLD_UPPER_FIRST_UNDERSCORES, state);
        }
        Template template = finderService.findTemplate(Assembler.SCREEN, data, app, component);
        return template;
    }

    /**
     * Returns the current state of the component.
     *
     * <p>The base implemenation always returns state "default" which is OK
     * for stateless components, and inintial state of stateful
     * components.</p>
     *
     * @param  data the RunData
     * @return current state of the component.
     */
    public String getState(RunData data)
        throws ProcessingException
    {
        return "Default";
    }

    /**
     * Runns the prepare&lt;state&gt;(RunData, Context) method of the child
     * class.
     */
    protected void prepareState(RunData data, Context context)
        throws ProcessingException
    {
        String state = getState(data);
        if(state == null || state.length() == 0)
        {
            cmsDataFactory.getCmsData(context).getComponent().error("wrong state name", null);
            return;
        }
        Method method = (Method)methodMap.get(state);
        if(method == null)
        {
            Class[] args = new Class[] { RunData.class, Context.class };
            try
            {
                method = getClass().getMethod("prepare"+state, args);
            }
            catch(NoSuchMethodException e)
            {
                throw new ProcessingException("method prepare"+state+
                                              "(RunData, Context) not declared in class "+
                                              getClass().getName(), e);
            }
            methodMap.put(state, method);
        }
        try
        {
            method.invoke(this, new Object[] { data, context });
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to invoke prepare"+state+
                                              "(RunData, Context) not declared in class "+
                                              getClass().getName(), e);
        }
    }

    /**
     * Default blank implementaion of prepareDefault method()
     */
    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
    }

    // configuration support methods ///////////////////////////////////////////////////////////////

    protected Parameters getConfiguration()
    {
        try
        {
            return prepareScreenConfig(data);
        }
        catch(ProcessingException e)
        {
            return null;
        }
    }

    protected void screenError(NavigationNodeResource currentNode, Context context, String message)
    throws ProcessingException
    {
        // TODO: Think of a better way of keeping the screen error messages
        message = message + ", embedded screen: "+this.getClass().getName();

        List messages = (List)(context.get(EmbeddedScreen.SCREEN_ERROR_MESSAGES_KEY));
        if(messages == null)
        {
            messages = new ArrayList();
        }
        messages.add(message);

        templatingContext.put(EmbeddedScreen.SCREEN_ERROR_MESSAGES_KEY, messages);
    }
}
