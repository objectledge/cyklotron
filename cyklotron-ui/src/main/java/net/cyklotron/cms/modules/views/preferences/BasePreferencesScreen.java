package net.cyklotron.cms.modules.views.preferences;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: BasePreferencesScreen.java,v 1.1 2005-01-24 04:34:31 pablo Exp $
 */
public class BasePreferencesScreen 
    extends BaseCMSScreen
{
    protected PreferencesService preferencesService;
    
    public BasePreferencesScreen()
    {
        preferencesService = (PreferencesService)Labeo.getBroker().
            getService(PreferencesService.SERVICE_NAME);
    }
}
