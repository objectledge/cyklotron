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
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Related file quick edit screen.
 *
 * @author <a href="mailto:piotr.plenik@gmail.com">Piotr Plenik</a>
 * @version $Id: FileQuickEdit.java,v 1.2 2007-11-18 21:24:42 rafal Exp $
 */ 
public class FileQuickEdit 
extends BaseFilesScreen
{
    
    public FileQuickEdit(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, filesService);   
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
    throws ProcessingException
	{
        long fileId = parameters.getLong("res_id", -1);
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
