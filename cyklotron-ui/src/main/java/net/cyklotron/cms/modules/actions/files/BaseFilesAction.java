package net.cyklotron.cms.modules.actions.files;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseFilesAction.java,v 1.1 2005-01-24 04:34:24 pablo Exp $
 */
public abstract class BaseFilesAction
    extends BaseCMSAction
{
    protected FilesService filesService;
    
    protected Logger log;
    
    public BaseFilesAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("files");
        filesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
    {
        try
        {
            long dirId = parameters.getLong("dir_id", -1);
            if(dirId == -1)
            {
                return coralSession.getUserSubject().hasRole(filesService.getFilesAdministrator(getSite(context)));    
            }
            else
            {
                Resource resource = coralSession.getStore().getResource(dirId);
                Permission permission = coralSession.getSecurity().
                    getUniquePermission("cms.files.write");
                return coralSession.getUserSubject().hasPermission(resource, permission);                    
            }            
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to view this screen",e);
            return false;
        }
    }

}


