package net.cyklotron.cms.modules.actions.files;

import net.cyklotron.cms.aggregation.AggregationException;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileAlreadyExistsException;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Uplad file action.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileAggregationCopy.java,v 1.2 2005-01-24 10:27:25 pablo Exp $
 */
public class FileAggregationCopy
    extends BaseFilesAction
{
    AggregationService aggregationService;
    
    public FileAggregationCopy()
    {
        aggregationService = (AggregationService)broker.getService(AggregationService.SERVICE_NAME);
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        long resourceId = parameters.getLong("res_id", -1);
        long parentId = parameters.getLong("parent_id", -1);
        
        try
        {
            Resource parent = coralSession.getStore().getResource(parentId);
            if(!(parent instanceof DirectoryResource))
            {
                route(data, "aggregation,ImportTarget", "invalid_directory");
                return;
            }
            FileResource source = FileResourceImpl.getFileResource(coralSession, resourceId);
            if(!aggregationService.canImport(source, parent, subject))
            {
                route(data, "aggregation,ImportTarget", "no_rights_to_import");
                return;
            }
            String targetName = parameters.get("target_name",source.getName());
            FileResource file = filesService.copyFile(source, targetName, (DirectoryResource)parent, subject);
            aggregationService.createImport(source, file, subject);
        }
        catch(FileAlreadyExistsException e)
        {
            route(data, "aggregation,ImportTarget", "already_exists");
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
        catch(AggregationException e)
        {
            log.error("AggregationException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            return;
        }
        templatingContext.put("result","copied_successfully");
    }
}

