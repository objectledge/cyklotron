package net.cyklotron.cms.modules.views.files;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Related file quick add screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileQuickAdd.java,v 1.1 2005-01-24 04:34:12 pablo Exp $
 */
public class FileQuickAdd
    extends BaseFilesScreen
{
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long directoryId = parameters.getLong("parent_id", -1L);
        if(directoryId == -1L)
        {
            throw new ProcessingException("parameter parent_id not found");
        }
        try
        {
            Resource directory = coralSession.getStore().getResource(directoryId);
            templatingContext.put("current_directory",directory);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
    }    
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
            long dirId = parameters.getLong("parent_id", -1);
            if(dirId == -1)
            {
                return true;    
            }
            else
            {
                Resource resource = coralSession.getStore().getResource(dirId);
                Permission permission = coralSession.getSecurity().
                    getUniquePermission("cms.files.write");
                return coralSession.getUserSubject().hasPermission(resource, permission);                    
            }
        }
        catch(Exception e)
        {
            log.error("Subject has no rights to view this screen",e);
            return false;
        }
    }
}

