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
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.structure.StructureService;

/**
 * update the file action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateFile.java,v 1.4 2005-03-08 10:51:58 pablo Exp $
 */
public class UpdateFile
    extends BaseFilesAction
{
    
    
    public UpdateFile(Logger logger, StructureService structureService,
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
            file.update();
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("ARLException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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
            logger.error("Subject has no rights to update the file", e);
            return false;
        }
    }

}

