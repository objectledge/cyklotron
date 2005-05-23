/*
 * Created on Apr 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.cyklotron.cms.modules.actions;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * 
 *  @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 *  @version $Id: SetEmbeddedScreen.java,v 1.3 2005-05-23 06:32:56 pablo Exp $
 */
public class SetEmbeddedScreen 
	extends BaseStructureAction
{
    protected PreferencesService preferencesService;

    protected IntegrationService integrationService;

    public SetEmbeddedScreen(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.preferencesService = preferencesService;
        this.integrationService = integrationService;
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        NavigationNodeResource node = getNode(context);

        try
        {
            long componentId = parameters.getLong("screen_id");
            ScreenResource screen =
                (ScreenResource)coralSession.getStore().getResource(componentId);
            ApplicationResource application = integrationService.getApplication(coralSession, screen);
            Parameters nodePrefs = preferencesService.getNodePreferences(node);
            nodePrefs.set("screen.app", application.getApplicationName());
            nodePrefs.set("screen.class", screen.getScreenName().replace(".",","));
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to set component class", e);
        }
    }

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return getCmsData(context).getNode().canModify(context, coralSession.getUserSubject());
    }
}
