package net.cyklotron.cms.modules.actions.files;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.DirectoryNotEmptyException;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.DirectoryResourceImpl;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.RootDirectoryResource;

/**
 * Delete directory action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DeleteDirectory.java,v 1.2 2005-01-24 10:27:25 pablo Exp $
 */
public class DeleteDirectory
    extends BaseFilesAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        long dirId = parameters.getLong("del_dir_id", -1);
        if(dirId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        long parentId = -1;
        Subject subject = coralSession.getUserSubject();
        try
        {
            DirectoryResource directory = DirectoryResourceImpl.getDirectoryResource(coralSession, dirId);
            if(directory instanceof RootDirectoryResource)
            {
                templatingContext.put("result","invalid_directory");
                return;
            }
            filesService.deleteDirectory(directory, subject);
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(DirectoryNotEmptyException e)
        {
            templatingContext.put("result","directory_not_empty");
            return;
        }
        catch(FilesException e)
        {
            log.error("FilesException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
    
    public boolean checkAccessRights(Context context)
    {
        try
        {
            long dirId = parameters.getLong("dir_id", -1);
            if(dirId == -1)
            {
                return true;
            }
            DirectoryResource directory = DirectoryResourceImpl.getDirectoryResource(coralSession, dirId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.files.delete");
            return coralSession.getUserSubject().hasPermission(directory, permission);
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to delete the directory", e);
            return false;
        }
    }


}

