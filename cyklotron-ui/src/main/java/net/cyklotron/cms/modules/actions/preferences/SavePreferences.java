package net.cyklotron.cms.modules.actions.preferences;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SavePreferences.java,v 1.1 2005-01-24 04:34:36 pablo Exp $
 */
public class SavePreferences
    extends BaseCMSAction
{
    /** preferenes service */
    private PreferencesService preferencesService;

    /** logging facility */
    private Logger log;


    public SavePreferences()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(PreferencesService.LOGGING_FACILITY);
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
    }


    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        Parameters pc = parameters;

        String application = pc.get("preferences.application","default");
        String component = pc.get("preferences.component","default");
        String instance = pc.get("preferences.instance","default");
        long target = pc.get("preferences.target").asLong(-2);
        if(target == -2)
        {
            templatingContext.put("result","parameter_not_found");
            return;

        }

        Parameters configuration = null;
        if(target == -1)
        {
            configuration = preferencesService.getSystemPreferences();
        }
        if(target == 0)
        {
            configuration = preferencesService.getUserPreferences(subject);
        }
        if(target > 0)
        {
            try
            {
                NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(coralSession, target);
                configuration = preferencesService.getNodePreferences(node);
            }
            catch(EntityDoesNotExistException e)
            {
                templatingContext.put("result","exception");
                templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
                log.error("ResourceException: ",e);
                return;
            }
        }
        String prefix = application + "." + component + "." + instance + ".";
        String[] keys = pc.getKeys();
        for(int i = 0; i < keys.length; i++)
        {
            if(keys[i].length() > 5 && keys[i].startsWith("conf."))
            {
                String key = prefix + keys[i].substring(5);
                configuration.set(key, pc.get(keys[i]));
            }
        }
        templatingContext.put("result","saved_successfully");
    }
}
