package net.cyklotron.cms.modules.views.appearance;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleConstants;
import net.cyklotron.cms.style.StyleService;

public abstract class BaseAppearanceScreen
    extends BaseCMSScreen
    implements StyleConstants
{
    /** style service */
    protected StyleService styleService;

    /** skin service */
    protected SkinService skinService;
    
    /** integration service */
    protected IntegrationService integrationService;
    
    /** templating service */
    protected Templating templating;

    public BaseAppearanceScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService,
        SkinService skinService, IntegrationService integrationService,
        Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.styleService = styleService;
        this.skinService = skinService;
        this.integrationService = integrationService;
        this.templating = templating;
    }
    
    /**
     * Checks the current user's privileges to viewing this screen.
     *
     * <p>The views in this package require that the current user has the
     * <code>cms.layout.administer</code> permission on the resource specified
     * by the style_id, layout_id or site_id (checked in that order).</p>
     *
     * @param data the RunData.
     * @throws ProcessingException if the privileges could not be determined.
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            Permission perm = coralSession.getSecurity().
                getUniquePermission("cms.layout.administer");
            long styleId = parameters.getLong("style_id", -1);
            if(styleId != -1)
            {
                Resource res = coralSession.getStore().getResource(styleId);
                return coralSession.getUserSubject().hasPermission(res, perm);
            }
            long layoutId = parameters.getLong("layout_id", -1);
            if(layoutId != -1)
            {
                Resource res = coralSession.getStore().getResource(layoutId);
                return coralSession.getUserSubject().hasPermission(res, perm);
            }

            SiteResource site = getSite();
            if(site != null)
            {
                return coralSession.getUserSubject().hasPermission(site, perm);
            }
            else
            {
                // permission required for configuring global components
                return checkAdministrator(coralSession);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check permissions", e);
        }
    }
}
