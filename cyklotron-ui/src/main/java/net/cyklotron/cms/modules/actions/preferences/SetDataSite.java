/*
 */
package net.cyklotron.cms.modules.actions.preferences;

import org.objectledge.pipeline.ProcessingException;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class SetDataSite extends BasePreferencesAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException, NotFoundException
    {
        Parameters sys = preferencesService.getSystemPreferences();
        sys.set("globalComponentsData", parameters.get("data_site"));
    }
}
