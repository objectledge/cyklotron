package net.cyklotron.cms.modules.actions.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationException;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileAlreadyExistsException;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Uplad file action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileAggregationCopy.java,v 1.3 2005-01-25 03:22:00 pablo Exp $
 */
public class FileAggregationCopy
    extends BaseFilesAction
{
    AggregationService aggregationService;
    
    
    
    public FileAggregationCopy(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService,
        AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory, filesService);
        this.aggregationService = aggregationService;
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        long resourceId = parameters.getLong("res_id", -1);
        long parentId = parameters.getLong("parent_id", -1);
        
        try
        {
            Resource parent = coralSession.getStore().getResource(parentId);
            if(!(parent instanceof DirectoryResource))
            {
                route(mvcContext, templatingContext, "aggregation,ImportTarget", "invalid_directory");
                return;
            }
            FileResource source = FileResourceImpl.getFileResource(coralSession, resourceId);
            if(!aggregationService.canImport(coralSession, source, parent, subject))
            {
                route(mvcContext, templatingContext, "aggregation,ImportTarget", "no_rights_to_import");
                return;
            }
            String targetName = parameters.get("target_name",source.getName());
            FileResource file = filesService.copyFile(coralSession, source, targetName, (DirectoryResource)parent);
            aggregationService.createImport(coralSession, source, file, subject);
        }
        catch(FileAlreadyExistsException e)
        {
            route(mvcContext, templatingContext, "aggregation,ImportTarget", "already_exists");
            return;
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
        catch(AggregationException e)
        {
            logger.error("AggregationException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","copied_successfully");
    }
}

