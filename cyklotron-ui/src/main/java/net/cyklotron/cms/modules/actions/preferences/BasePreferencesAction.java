/*
 */
package net.cyklotron.cms.modules.actions.preferences;

import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public abstract class BasePreferencesAction extends BaseCMSAction
{
    protected PreferencesService preferencesService;
    
    public BasePreferencesAction()
    {
        preferencesService = (PreferencesService)Labeo.getBroker().
            getService(PreferencesService.SERVICE_NAME);
    }
    
    public boolean checkAccess(RunData data) throws ProcessingException
    {
        return super.checkAdministrator(context, coralSession);
    }
}
