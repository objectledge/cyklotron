package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.files.FilesConstants;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;


/**
 * The base screen class for files application screens
 */
public class BaseFilesScreen
    extends BaseCMSScreen
    implements FilesConstants
{
    protected Logger log;
    
    protected FilesService filesService;

    protected PreferencesService preferencesService;

    public BaseFilesScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(FilesService.LOGGING_FACILITY);
        filesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
        preferencesService = (PreferencesService)broker.getService(PreferencesService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
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
                resource = filesService.getFilesRoot(site);
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
            log.error("Subject has no rights to view this screen",e);
            return false;
        }
    }

}
