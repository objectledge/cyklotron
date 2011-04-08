package net.cyklotron.cms.modules.actions.catalogue;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.catalogue.CatalogueConfigResource;
import net.cyklotron.cms.catalogue.CatalogueConfigResourceImpl;
import net.cyklotron.cms.catalogue.CatalogueService;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;

public class UpdateConfig
    extends BaseCMSAction
{
    private final CatalogueService catalogueService;

    public UpdateConfig(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CatalogueService catalogueService)
    {
        super(logger, structureService, cmsDataFactory);
        this.catalogueService = catalogueService;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        Resource configRoot = catalogueService.getConfigRoot(cmsData.getSite(), coralSession);

        // ///////////////////////////////////////////////////////////////////////////////////// //
        String name = "";
        CategoryResource category = null;
        PoolResource searchPool = null;
        
        name = parameters.get("name", "");
        templatingContext.put("name", name);

        try
        {
            if(parameters.get("category", "").length() > 0)
            {
                category = CategoryResourceImpl.getCategoryResource(coralSession,
                    parameters.getLong("category"));
                templatingContext.put("category", category);
            }
            
            if(parameters.get("search_pool", "").length() > 0)
            {
                searchPool = PoolResourceImpl.getPoolResource(coralSession,
                    parameters.getLong("search_pool"));
                templatingContext.put("search_pool", searchPool);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException(e);
        }

        String cid = parameters.get("cid", "new");

        // ///////////////////////////////////////////////////////////////////////////////////// //
        String problem = null;
        if(name.length() == 0)
        {
            problem = "name_empty";
        }
        if(!coralSession.getStore().isValidResourceName(name))
        {
            problem = "name_invalid";
        }
        if(cid.equals("new") && coralSession.getStore().getResource(configRoot, name).length > 0)
        {
            problem = "name_in_use";
        }
        if(category == null)
        {
            problem = "cateogory_not_selected";
        }
        if(searchPool == null)
        {
            problem = "search_pool_not_selected";
        }

        // ///////////////////////////////////////////////////////////////////////////////////// //
        if(problem == null)
        {
            try
            {
                if(cid.equals("new"))
                {
                    catalogueService.createCatalogue(cmsData.getSite(), name, category, searchPool,
                        coralSession);
                }
                else
                {
                    CatalogueConfigResource config = CatalogueConfigResourceImpl
                    .getCatalogueConfigResource(coralSession, Long.parseLong(cid));
                    catalogueService.updateCatalogue(config, name, category, searchPool, coralSession);
                }
            }
            catch(Exception e)
            {
                problem = "exception";
                templatingContext.put("trace", new StackTrace(e).toString());
            }
        }

        // ///////////////////////////////////////////////////////////////////////////////////// //        
        if(problem == null)
        {
            templatingContext.put("result", "success");
        }
        else
        {
            templatingContext.put("result", problem);
            mvcContext.setView("catalogue.Config");
        }
        
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("catalogue"))
        {
            logger.debug("Application 'catalogue' not enabled in site");
            return false;
        }
        return checkAdministrator(context);
    }
}
