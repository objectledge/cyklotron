package net.cyklotron.cms.modules.components;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.labeo.services.templating.Context;
import net.labeo.services.templating.Template;
import net.labeo.services.webcore.FinderService;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.Assembler;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;

/**
 * The base class for skinable CMS components
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: SkinableCMSComponent.java,v 1.1 2005-01-24 04:34:00 pablo Exp $
 */
public abstract class SkinableCMSComponent
    extends BaseCMSComponent
{
    protected SkinService skinService;

    protected FinderService finderService;

    private Map methodMap = new HashMap();

    public SkinableCMSComponent()
    {
        skinService = (SkinService)broker.getService(SkinService.SERVICE_NAME);
        finderService = (FinderService)broker.getService(FinderService.SERVICE_NAME);
    }

    /**
     * Returns a components template.
     *
     * @param data the RunData
     * @return a template to be used for rendering this block.
     */
    public Template getTemplate(RunData data)
        throws NotFoundException
    {
        CmsData cmsData = null;
        SiteResource site = null;
        CmsComponentData componentData = null;
        String skin = "default"; 
        try
        {
            cmsData = getCmsData();
            componentData = cmsData.getComponent();
            site = cmsData.getSite();
            if(site == null || componentData.isGlobal())
            {
                site = cmsData.getGlobalComponentsDataSite();
                if(site == null)
                {
                    // no site - this may be a not skinnable component.
                    return super.getTemplate(data);
                }
            }
            // 1. get skin name
            skin = skinService.getCurrentSkin(site);
        }
        catch(Exception e)
        {
            return super.getTemplate(data);
        }


        // 2. get component info
        String app = componentData.getApp();
        String component = componentData.getClazz();
        String variant = componentData.getVariant();
        String state = "Default";
        try
        {
            state = getState(data);
        }
        catch(ProcessingException e)
        {
            throw new NotFoundException("failed to determine state", e);
        }

        // 3. get template object
        Template templ = null;

        try
        {
            // if skin defines a template for the variant
            if(skinService.hasComponentTemplate(site, skin, app, component, variant, state))
            {
                templ = skinService.getComponentTemplate(site, skin, app, component, variant, state);
            }
            else
            {
                templ = getAppComponentTemplate(data, app, component, state);
            }
        }
        catch(Exception e)
        {
            log.error("failed to lookup component template for component "+
                      app+":"+component+" site "+site.getName()+" skin "+skin+
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

    protected Template getAppComponentTemplate(RunData data, String app, String component, String state)
        throws NotFoundException
    {
        if(!state.equalsIgnoreCase("Default"))
        {
            component = component + StringUtils.
                foldCase(StringUtils.FOLD_UPPER_FIRST_UNDERSCORES, state);
        }
        Template template = finderService.findTemplate(Assembler.COMPONENT, data, app, component);
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
}
