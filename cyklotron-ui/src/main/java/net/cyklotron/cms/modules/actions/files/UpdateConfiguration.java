package net.cyklotron.cms.modules.actions.files;

import java.util.StringTokenizer;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.RootDirectoryResourceImpl;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

public class UpdateConfiguration
    extends BaseCMSAction
{
    private final FilesService filesService;

    private final CoralSessionFactory coralSessionFactory;

    public UpdateConfiguration(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService,
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory);
        this.filesService = filesService;
        this.coralSessionFactory = coralSessionFactory;
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

            ResourceList<CategoryResource> frontCategoriesList = getCategories(parameters,
                "front_categories_ids", coralSession, coralSessionFactory);
            filesMap.setFrontCategories(frontCategoriesList);
            filesMap.update();
        }
        catch(FilesException e)
        {
            throw new ProcessingException(e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException(e);
        }
    }

    private static ResourceList<CategoryResource> getCategories(Parameters parameters,
        String parameterName, CoralSession coralSession, CoralSessionFactory coralSessionFactory)
        throws EntityDoesNotExistException
    {
        ResourceList<CategoryResource> categories = new ResourceList<CategoryResource>(
            coralSessionFactory);
        String ids = parameters.get(parameterName, "");
        if(!ids.isEmpty())
        {
            CategoryResource category = null;
            StringTokenizer st = new StringTokenizer(ids, " ");
            while(st.hasMoreTokens())
            {
                category = CategoryResourceImpl.getCategoryResource(coralSession,
                    Long.parseLong(st.nextToken()));
                if(category != null)
                {
                    categories.add(category);
                }
            }
        }
        return categories;
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
