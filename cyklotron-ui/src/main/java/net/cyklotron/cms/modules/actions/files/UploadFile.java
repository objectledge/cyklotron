package net.cyklotron.cms.modules.actions.files;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.upload.UploadContainer;
import net.labeo.services.upload.UploadService;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileAlreadyExistsException;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;

/**
 * Upload file action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UploadFile.java,v 1.2 2005-01-24 10:27:25 pablo Exp $
 */
public class UploadFile
    extends BaseFilesAction
{
    private UploadService uploadService;

    public UploadFile()
    {
        uploadService = (UploadService)broker.getService(UploadService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        boolean unpackZip = parameters.getBoolean("unpack", false);
        String description = parameters.get("description","");
        long dirId = parameters.getLong("dir_id", -1);
        if(dirId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        UploadContainer item = uploadService.getItem(data,"item1");
        if(item == null)
        {
            templatingContext.put("result","not_uploaded_correctly");
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
			if(!filesService.isValid(item.getFileName()))
			{
				templatingContext.put("result","invalid_file_name");
				return;
			}
            String encoding = parameters.get("encoding",data.getEncoding());
            if(unpackZip && item.getFileName().endsWith(".zip"))
            {
                filesService.unpackZipFile(item.getInputStream(), encoding, (DirectoryResource)parent, subject);
            }
            else
            {
                FileResource file = filesService.createFile(item.getFileName(), item.getInputStream(),
                                                           item.getMimeType(), encoding ,(DirectoryResource)parent, subject);
                file.setDescription(description);
                file.setLocale(parameters.get("locale",i18nContext.getLocale()().toString()));
                file.update(subject);
            }
        }
        catch(FileAlreadyExistsException e)
        {
            templatingContext.put("result","already_exists");
            return;
        }
        catch(Exception e)
        {
            log.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","uploaded_successfully");
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

