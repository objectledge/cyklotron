package net.cyklotron.cms.modules.views.files;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.FileResourceImpl;

/**
 *
 */
public class EditFile
    extends BaseFilesScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long fileId = parameters.getLong("fid", -1);
        if(fileId == -1)
        {
            throw new ProcessingException("File id not found");
        }
        try
        {
            templatingContext.put("file", FileResourceImpl.getFileResource(coralSession,fileId));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("failed to retrieve the file resource", e);
        }
    }    
}

