package net.cyklotron.cms.modules.actions.category.query;

import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

public class CategoryQueryPoolUpdateOrder
    extends BaseCategoryQueryAction
{

    public CategoryQueryPoolUpdateOrder(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService,
        CategoryService categoryService, SiteService siteService, 
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, categoryQueryService, categoryService,
                        siteService);
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CategoryQueryPoolResource pool = CategoryQueryUtil.getPool(coralSession, parameters);
        String key = "cms.category.query.pool.order." + pool.getIdString();
        List<Long> list = (List<Long>)httpContext.getSessionAttribute(key);
        int pos = parameters.getInt("pos", 1) - 1;
        int dir = parameters.getInt("dir", 0);
        Long id = list.remove(pos);
        switch (dir)
        {
        case -1:
            list.add(pos-1,id);
            break;
        case 1:
            list.add(pos+1,id);
            break;
        case -2:
            list.add(0,id);
            break;
        case 2:
            list.add(id);
            break;
        }
    }
    
    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.pool.modify");
    }
}
