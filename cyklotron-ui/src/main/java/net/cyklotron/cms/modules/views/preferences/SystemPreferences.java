package net.cyklotron.cms.modules.views.preferences;

import net.labeo.services.resource.Role;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: SystemPreferences.java,v 1.3 2005-01-25 11:24:08 pablo Exp $
 */
public class SystemPreferences 
    extends BasePreferencesScreen
{
    /* overriden */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters conf = preferencesService.getSystemPreferences();
        templatingContext.put("config", conf.getContents());
    }    
    
    /* overriden */
    public boolean checkAccessRights(Context context) 
        throws ProcessingException
    {
        Role administrator = coralSession.getSecurity().
            getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(administrator);
    }
}
