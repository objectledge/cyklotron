package net.cyklotron.cms.modules.actions.fixes;

import java.util.ArrayList;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Converts the navigation configurations to fit new navigation functionality.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FixNavigationPreferences.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class FixNavigationPreferences extends BaseCMSAction
{
    PreferencesService preferencesService;

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        preferencesService = (PreferencesService)data.getBroker().
            getService(PreferencesService.SERVICE_NAME);
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        try
        {
            SiteResource site = getSite(context);
            ArrayList stack = new ArrayList();
            stack.add(structureService.getRootNode(site));
            while(stack.size() > 0)
            {
                NavigationNodeResource node = (NavigationNodeResource)stack.remove(stack.size()-1);
                Resource[] children = coralSession.getStore().getResource(node);
                for(int i=0; i<children.length; i++)
                {
                    stack.add(children[i]);
                }
                if(fixNode(node))
                {
                    node.update(subject);
                }
            }
            templatingContext.put("result", "success");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }

    public boolean fixNode(NavigationNodeResource node)
        throws ProcessingException
    {
        boolean update = false;
        
        Parameters conf = preferencesService.getNodePreferences(node);
        // get component prefix
        Parameters components = conf.getSubset("component.");
        String[] instanceNames = components.getSubsetNames();
        // get all instances
        for(int i=0; i<instanceNames.length; i++)
        {
            Parameters instance = components.getSubset(instanceNames[i]+".");
            String app = instance.get("app",null);
            String clazz = instance.get("class",null);
            
            // get instances which are navigations
            if(app != null && clazz != null && app.equals("cms") && clazz.startsWith("structure,"))
            {
                Parameters compConf = instance.getSubset("config.cms."+clazz.replace(',','.')+".");
                if(!compConf.containsKey("rootConfigType"))
                {
                    String path = compConf.get("rootPath","");
                    String rootConfigType = "rootLevel";
                    if(path.length() > 0 && path.charAt(0) == '/')
                    {
                        rootConfigType = "rootPath";
                    }
                    compConf.set("rootConfigType", rootConfigType);
                    update = true;
                }
            }
        }
        
        return update;
    }
}
