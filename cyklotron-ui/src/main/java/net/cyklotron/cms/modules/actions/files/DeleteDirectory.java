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
import net.cyklotron.cms.files.DirectoryNotEmptyException;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.DirectoryResourceImpl;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.RootDirectoryResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * Delete directory action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DeleteDirectory.java,v 1.3 2005-01-25 03:22:00 pablo Exp $
 */
public class DeleteDirectory
    extends BaseFilesAction
{
    
    public DeleteDirectory(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService)
    {
        super(logger, structureService, cmsDataFactory, filesService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
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
            filesService.deleteDirectory(coralSession, directory);
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("ARLException: ",e);
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
            logger.error("FilesException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
    
    public boolean checkAccessRights(Context context)
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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
            logger.error("Subject has no rights to delete the directory", e);
            return false;
        }
    }


}

