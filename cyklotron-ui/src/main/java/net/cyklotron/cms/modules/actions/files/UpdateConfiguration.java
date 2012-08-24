package net.cyklotron.cms.modules.actions.files;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.RootDirectoryResource;
import net.cyklotron.cms.files.RootDirectoryResourceImpl;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;

public class UpdateConfiguration
    extends BaseCMSAction
{
    private final FilesService filesService;

    public UpdateConfiguration(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService)
    {
        super(logger, structureService, cmsDataFactory);
        this.filesService = filesService;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            FilesMapResource filesMap = filesService.getFilesRoot(coralSession, cmsData.getSite());

            long expandedDirectoryId = parameters.getLong("expanded_directory_id", -1);
            DirectoryResource expandedDirectory = null;

            if(expandedDirectoryId != -1)
            {
                expandedDirectory = RootDirectoryResourceImpl.getDirectoryResource(coralSession,
                    expandedDirectoryId);
                filesMap.setExpandedDirectory(expandedDirectory);
            }
            else
            {
                filesMap.setExpandedDirectory(null);
            }
            filesMap.update();
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(cmsData.getSite().getAdministrator());
    }
}
