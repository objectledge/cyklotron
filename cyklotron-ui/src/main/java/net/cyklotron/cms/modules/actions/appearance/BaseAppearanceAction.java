package net.cyklotron.cms.modules.actions.appearance;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleConstants;
import net.cyklotron.cms.style.StyleService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseAppearanceAction.java,v 1.3 2005-02-08 22:06:21 rafal Exp $
 */
public abstract class BaseAppearanceAction
    extends BaseCMSAction
    implements StyleConstants
{
    /** logging facility */
    protected Logger log;

    /** style service */
    protected StyleService styleService;

    /** file service */
    protected FileSystem fileService;

    /** skin service */
    protected SkinService skinService;

    /** integration service */
    protected IntegrationService integrationService;

    public BaseAppearanceAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory);
        this.styleService = styleService;
        this.fileService = fileSystem;
        this.skinService = skinService;
        this.integrationService = integrationService;
    }

    /**
     * Checks the current user's privileges to running this action.
     *
     * <p>The actions in this package require that the current user has the
     * <code>cms.layout.administer</code> permission on the resource specified
     * by the style_id, layout_id or site_id (checked in that order).</p>
     *
     * @param context the request context.
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

            SiteResource site = getSite(context);
            if(site != null)
            {
                return coralSession.getUserSubject().hasPermission(site, perm);
            }
            else
            {
                // permission required for configuring global components
                return checkAdministrator(context);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check permissions", e);
        }
    }
}


