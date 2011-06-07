package net.cyklotron.cms.modules.actions.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Delete the file action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class DeleteFiles
    extends BaseFilesAction
{
    
    
    public DeleteFiles(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService)
    {
        super(logger, structureService, cmsDataFactory, filesService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        boolean file_in_use = false;
        boolean no_rights = false;
        long[] fileIds = parameters.getLongs("delete_id");
        Subject subject = coralSession.getUserSubject();
        Permission permission = coralSession.getSecurity().getUniquePermission("cms.files.delete");
        try
        {
            for(long fileId: fileIds)
            {
                FileResource file = FileResourceImpl.getFileResource(coralSession, fileId);
                if(subject.hasPermission(file, permission))
                {
                    filesService.deleteFile(coralSession, file);
                }
                else
                {
                    no_rights = true;
                }
            }
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(FilesException e)
        {
            logger.error("FilesException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        catch(Exception e)
        {
            String message = (new StackTrace(e)).toString();
            if(message != null && message.contains("violates foreign key constraint"))
            {
                file_in_use = true;
            }
            else
            {
            	logger.error("Exception: ",e);
                templatingContext.put("result","exception");
                templatingContext.put("trace",new StackTrace(e));
                return;
            }
        }
        if(file_in_use || no_rights)
        {
            templatingContext.put("result","some_files_not_deleted");
        }
        else
        {
            templatingContext.put("result","deleted_successfully");
        }
    }

    public boolean checkAccessRights(Context context)
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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
            logger.error("Subject has no rights to delete the file", e);
            return false;
        }
    }

}

