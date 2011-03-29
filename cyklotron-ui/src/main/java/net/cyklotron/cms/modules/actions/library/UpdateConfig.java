package net.cyklotron.cms.modules.actions.library;

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
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.library.LibraryConfigResource;
import net.cyklotron.cms.library.LibraryService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;

public class UpdateConfig
    extends BaseCMSAction
{
    private final LibraryService libraryService;

    public UpdateConfig(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, LibraryService libraryService)
    {
        super(logger, structureService, cmsDataFactory);
        this.libraryService = libraryService;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        LibraryConfigResource config = libraryService.getConfig(cmsData.getSite(), coralSession);

        try
        {
            if(parameters.get("category","").length() > 0)
            {
                CategoryResource category = CategoryResourceImpl.getCategoryResource(coralSession, parameters.getLong("category"));
                config.setCategory(category);
            }
            if(parameters.get("search_pool","").length() > 0)
            {
                PoolResource searchPool = PoolResourceImpl.getPoolResource(coralSession, parameters.getLong("search_pool"));
                config.setSearchPool(searchPool);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("invalid parameter", e);
        }
        
        config.update();        
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("library"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(coralSession, coralSession.getUserSubject());
        }
        else
        {
            return checkAdministrator(context);
        }
    }
}
