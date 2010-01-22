package net.cyklotron.cms.modules.actions.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseFilesAction.java,v 1.2 2005-01-24 10:27:25 pablo Exp $
 */
public abstract class BaseFilesAction
    extends BaseCMSAction
{
    protected FilesService filesService;
    
    public BaseFilesAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService)
    {
        super(logger, structureService, cmsDataFactory);
        this.filesService = filesService;
    }

    public boolean checkAccessRights(Context context)
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            long dirId = parameters.getLong("dir_id", -1);
            if(dirId == -1)
            {
                return coralSession.getUserSubject().hasRole(filesService.getFilesAdministrator(coralSession, getSite(context)));    
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


