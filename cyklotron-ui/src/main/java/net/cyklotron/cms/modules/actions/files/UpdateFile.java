package net.cyklotron.cms.modules.actions.files;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;

/**
 * update the file action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateFile.java,v 1.2 2005-01-24 10:27:25 pablo Exp $
 */
public class UpdateFile
    extends BaseFilesAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        long fileId = parameters.getLong("fid", -1);
        if(fileId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        Subject subject = coralSession.getUserSubject();
        try
        {
            FileResource file = FileResourceImpl.getFileResource(coralSession, fileId);
            file.setDescription(parameters.get("description",""));
            file.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
    {
        try
        {
            long fileId = parameters.getLong("fid", -1);
            if(fileId == -1)
            {
                return true;
            }
            FileResource file = FileResourceImpl.getFileResource(coralSession, fileId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.files.modify");
            return coralSession.getUserSubject().hasPermission(file, permission);
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to update the file", e);
            return false;
        }
    }

}

