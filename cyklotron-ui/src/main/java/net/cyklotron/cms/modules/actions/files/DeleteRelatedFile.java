package net.cyklotron.cms.modules.actions.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.UploadLimitExceededException;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.related.RelatedConstants;
import net.cyklotron.cms.structure.StructureService;

/**
 * Delete the related file action.
 * 
 * @author <a href="mailo:lukasz@caltha.pl">Łukasz Urbański</a>
 * @version $Id: DeleteRelatedFile.java
 */
public class DeleteRelatedFile
    extends DeleteFile
    implements RelatedConstants
{

    public DeleteRelatedFile(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService)
    {
        super(logger, structureService, cmsDataFactory, filesService);

    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long resId = parameters.getLong("res_id", -1L);
            long fileId = parameters.getLong("file_id", -1);

            Resource resource = coralSession.getStore().getResource(resId);
            Resource resourceFile = coralSession.getStore().getResource(fileId);

            ResourceSelectionState relatedState = ResourceSelectionState.getState(context,
                RELATED_SELECTION_STATE + ":" + resource.getIdString());
            relatedState.update(parameters);

            super.execute(context, parameters, mvcContext, templatingContext, httpContext,
                coralSession);

            relatedState.remove(resourceFile);
        }
        catch(EntityDoesNotExistException e)
        {
            logger.error("ARLException: ", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        catch(Exception e)
        {
            logger.error("Exception: ", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            return;
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
            Permission permission = coralSession.getSecurity().getUniquePermission(
                "cms.files.delete");
            return coralSession.getUserSubject().hasPermission(file, permission);
        }
        catch(Exception e)
        {
            logger.error("Subject has no rights to delete the file", e);
            return false;
        }
    }

}
