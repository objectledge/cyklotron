package net.cyklotron.cms.modules.actions.appearance;

import net.labeo.Labeo;
import net.labeo.services.file.FileService;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleConstants;
import net.cyklotron.cms.style.StyleService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseAppearanceAction.java,v 1.1 2005-01-24 04:34:33 pablo Exp $
 */
public abstract class BaseAppearanceAction
    extends BaseCMSAction
    implements Secure, StyleConstants
{
    /** logging facility */
    protected Logger log;

    /** style service */
    protected StyleService styleService;

    /** file service */
    protected FileService fileService;

    /** skin service */
    protected SkinService skinService;

    /** integration service */
    protected IntegrationService integrationService;

    public BaseAppearanceAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(StyleService.LOGGING_FACILITY);
        styleService = (StyleService)broker.
            getService(StyleService.SERVICE_NAME);
        fileService = (FileService)broker.
            getService(FileService.SERVICE_NAME);
        skinService = (SkinService)Labeo.getBroker().
            getService(SkinService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.
            getService(IntegrationService.SERVICE_NAME);
    }

    /**
     * Checks the current user's privileges to running this action.
     *
     * <p>The actions in this package require that the current user has the
     * <code>cms.layout.administer</code> permission on the resource specified
     * by the style_id, layout_id or site_id (checked in that order).</p>
     *
     * @param data the RunData.
     * @throws ProcessingException if the privileges could not be determined.
     */
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
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

            SiteResource site = getSite(context);
            if(site != null)
            {
                return coralSession.getUserSubject().hasPermission(site, perm);
            }
            else
            {
                // permission required for configuring global components
                return checkAdministrator(context, coralSession);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check permissions", e);
        }
    }
}


