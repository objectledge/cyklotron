package net.cyklotron.cms.modules.actions.preferences;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: SavePreferences.java,v 1.4 2005-03-23 09:14:03 pablo Exp $
 */
public class SavePreferences
    extends BaseCMSAction
{
    /** preferenes service */
    private PreferencesService preferencesService;

    public SavePreferences(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PreferencesService preferencesService)
    {
        super(logger, structureService, cmsDataFactory);
        this.preferencesService = preferencesService;
    }


    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        Parameters pc = parameters;

        String application = pc.get("preferences.application","default");
        String component = pc.get("preferences.component","default");
        String instance = pc.get("preferences.instance","default");
        long target = pc.getLong("preferences.target",-2);
        if(target == -2)
        {
            templatingContext.put("result","parameter_not_found");
            return;

        }

        Parameters configuration = null;
        if(target == -1)
        {
            configuration = preferencesService.getSystemPreferences(coralSession);
        }
        if(target == 0)
        {
            configuration = preferencesService.getUserPreferences(coralSession, subject);
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
                templatingContext.put("trace",new StackTrace(e));
                logger.error("ResourceException: ",e);
                return;
            }
        }
        String prefix = application + "." + component + "." + instance + ".";
        String[] keys = pc.getParameterNames();
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
