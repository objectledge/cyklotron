package net.cyklotron.cms.modules.actions.files;

import java.sql.SQLException;

import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Delete the file action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DeleteFile.java,v 1.1 2005-01-24 04:34:24 pablo Exp $
 */
public class DeleteFile
    extends BaseFilesAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        long fileId = parameters.getLong("file_id", -1);
        if(fileId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        Subject subject = coralSession.getUserSubject();
        try
        {
            FileResource file = FileResourceImpl.getFileResource(coralSession, fileId);
            filesService.deleteFile(file, subject);
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        catch(FilesException e)
        {
            log.error("FilesException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        catch(Exception e)
        {
        	log.error("Exception: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccess(RunData data)
    {
        try
        {
            long fileId = parameters.getLong("file_id", -1);
            if(fileId == -1)
            {
                return true;
            }
            FileResource file = FileResourceImpl.getFileResource(coralSession, fileId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.files.delete");
            return coralSession.getUserSubject().hasPermission(file, permission);
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to delete the file", e);
            return false;
        }
    }

}

