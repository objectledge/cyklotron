/*
 */
package net.cyklotron.cms.modules.actions.preferences;

import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class RemoveComponent extends BasePreferencesAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException, NotFoundException
    {
        Parameters sys = preferencesService.getSystemPreferences();
        String component = parameters.get("component");    
        sys.remove("globalComponents", component);
    }
}
