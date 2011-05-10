package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesConstants;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;


/**
 * The base screen class for files application screens
 */
public abstract class BaseFilesScreen
    extends BaseCMSScreen
    implements FilesConstants
{
    protected FilesService filesService;

    public BaseFilesScreen(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.filesService = filesService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site selected");
        }
        try
        {
            long dirId = parameters.getLong("dir_id", -1);
            Resource resource = null;
            if(dirId == -1)
            {
                resource = filesService.getFilesRoot(coralSession, site);
            }
            else
            {
                resource = coralSession.getStore().getResource(dirId);
            }
            Permission permission = coralSession.getSecurity().
                    getUniquePermission("cms.files.read");
            return coralSession.getUserSubject().hasPermission(resource, permission);                    
        }
        catch(Exception e)
        {
            logger.error("Subject has no rights to view this screen",e);
            return false;
        }
    }

}
