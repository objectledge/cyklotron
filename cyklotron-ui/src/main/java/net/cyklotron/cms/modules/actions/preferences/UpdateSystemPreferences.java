package net.cyklotron.cms.modules.actions.preferences;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.DefaultParameters;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.LoadingException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: UpdateSystemPreferences.java,v 1.1 2005-01-24 04:34:36 pablo Exp $
 */
public class UpdateSystemPreferences 
    extends BasePreferencesAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        Parameters conf = preferencesService.getSystemPreferences();
        String config = parameters.get("config","");
        try
        {
            conf.clear();
            conf.addAll(new DefaultParameters(config), true);
        }
        catch(LoadingException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
        if(context.containsKey("result"))
        {
            data.setView("preferences,SystemPrefernces");
        }
    }
}
