package net.cyklotron.cms.modules.actions.files;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileAlreadyExistsException;
import net.cyklotron.cms.files.FilesException;

/**
 * Create the directory action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CreateDirectory.java,v 1.2 2005-01-24 10:27:25 pablo Exp $
 */
public class CreateDirectory
    extends BaseFilesAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        String name = parameters.get("name","");
        if(name.equals(""))
        {
            templatingContext.put("result","directory_name_empty");
            return;
        }
		if(!filesService.isValid(name))
		{
			templatingContext.put("result","invalid_directory_name");
			return;
		}

        String description = parameters.get("description","");
        long dirId = parameters.getLong("dir_id", -1);
        if(dirId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        Subject subject = coralSession.getUserSubject();
        
        try
        {
            Resource parent = coralSession.getStore().getResource(dirId);
            if(!(parent instanceof DirectoryResource))
            {
                templatingContext.put("result","invalid_directory");
                return;
            }
            DirectoryResource directory = filesService.createDirectory(name, (DirectoryResource)parent, subject);
            directory.setDescription(description);
            directory.update(subject);
        }
        catch(FileAlreadyExistsException e)
        {
            templatingContext.put("result","already_exists");
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(FilesException e)
        {
            log.error("FilesException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","created_successfully");
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
            Resource parent = coralSession.getStore().getResource(dirId);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.files.write");
            return coralSession.getUserSubject().hasPermission(parent, permission);
        }
        catch(Exception e)
        {
            log.error("Subject has no rights write in the directory", e);
            return false;
        }
    }

}
