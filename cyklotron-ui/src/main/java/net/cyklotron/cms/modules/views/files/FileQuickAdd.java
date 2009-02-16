package net.cyklotron.cms.modules.views.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Related file quick add screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileQuickAdd.java,v 1.5 2005-03-08 11:02:38 pablo Exp $
 */
public class FileQuickAdd
    extends BaseFilesScreen
{
    
    public FileQuickAdd(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, filesService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
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
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
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
            logger.error("Subject has no rights to view this screen",e);
            return false;
        }
    }
}

