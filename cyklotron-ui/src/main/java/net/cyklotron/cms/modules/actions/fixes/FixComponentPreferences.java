package net.cyklotron.cms.modules.actions.fixes;

import java.util.ArrayList;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Converts the component configuration found in the node preferences to the
 * new naming convention.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: FixComponentPreferences.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class FixComponentPreferences extends BaseCMSAction
{
    PreferencesService preferencesService;

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        preferencesService = (PreferencesService)data.getBroker().
            getService(PreferencesService.SERVICE_NAME);
        Context context = data.getContext();
        try
        {
            SiteResource site = getSite(context);
            ArrayList stack = new ArrayList();
            stack.add(structureService.getRootNode(site));
            while(stack.size() > 0)
            {
                NavigationNodeResource node = (NavigationNodeResource)stack.
                    remove(stack.size()-1);
                Resource[] children = coralSession.getStore().getResource(node);
                for(int i=0; i<children.length; i++)
                {
                    stack.add(children[i]);
                }
                fixNode(node);
            }
            templatingContext.put("result", "success");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }

    public void fixNode(NavigationNodeResource node)
        throws ProcessingException
    {
        Parameters conf = preferencesService.getNodePreferences(node);
        String[] prefices = conf.getSubsetNames();
        outer: for(int i=0; i<prefices.length; i++)
        {
            String prefix = prefices[i];
            if(prefix.equals("component") || prefix.equals("screen") ||
               prefix.equals("site"))
            {
                continue outer;
            }
            Parameters compConf = conf.getSubset(prefix+".");
            String[] compProps = compConf.getKeys();
            Parameter app = compConf.get("app");
            Parameter component = compConf.get("class");
            if(!app.isDefined() || !component.isDefined())
            {
                Parameters combined = preferencesService.getCombinedNodePreferences(node);
                app = combined.get("component."+prefix+".app");
                component = combined.get("component."+prefix+".class");
            }
            if(app.isDefined() && component.isDefined())
            {
                inner: for(int j=0; j<compProps.length; j++)
                {
                    if(compProps[j].equals("app"))
                    {
                        conf.set("component."+prefix+".app", app);
                        continue inner;
                    }
                    if(compProps[j].equals("class"))
                    {
                        conf.set("component."+prefix+".class", component);
                        continue inner;
                    }
                    if(compProps[j].equals("component_variant"))
                    {
                        Parameter variant = compConf.get("component_variant");
                        conf.set("component."+prefix+".variant."+app.asString()+
                                 "."+component.asString().replace(',','.'), variant);
                        continue inner;
                    }
                    conf.set("component."+prefix+".config."+app.asString()+
                             "."+component.asString().replace(',','.')+"."+compProps[j],
                             compConf.get(compProps[j]));
                }
            }
            compConf.clear();
        }
    }
}
