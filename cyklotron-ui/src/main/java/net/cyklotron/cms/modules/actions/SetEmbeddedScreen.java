/*
 * Created on Apr 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.cyklotron.cms.modules.actions;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * 
 *  @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 *  @version $Id: SetEmbeddedScreen.java,v 1.1 2005-01-24 04:34:15 pablo Exp $
 */
public class SetEmbeddedScreen 
	extends BaseStructureAction
{
    protected PreferencesService preferencesService;

    protected IntegrationService integrationService;

    
    
    public SetEmbeddedScreen()
    {
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        NavigationNodeResource node = getNode(context);

        try
        {
            long componentId = parameters.getLong("screen_id");
            ScreenResource screen =
                (ScreenResource)coralSession.getStore().getResource(componentId);
            ApplicationResource application = integrationService.getApplication(screen);
            Parameters nodePrefs = preferencesService.getNodePreferences(node);
            nodePrefs.set("screen.app", application.getApplicationName());
            nodePrefs.set("screen.class", screen.getScreenName());
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to set component class", e);
        }
    }

    public boolean checkAccess(RunData data) throws ProcessingException
    {
        return getCmsData(context).getNode(context).canModify(coralSession.getUserSubject());
    }
}
