package net.cyklotron.cms.modules.views.category.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

public class CategoryQueryPoolReorder
    extends BaseCMSScreen
{
    public CategoryQueryPoolReorder(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
    }
    
    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        CategoryQueryPoolResource pool = CategoryQueryUtil.getPool(coralSession, parameters);
        templatingContext.put("pool", pool);
        String key = "cms.category.query.pool.order." + pool.getIdString();
        List<Long> queryIds;
        Map<Long, Resource> queryObjs = new HashMap<Long,Resource>();
        if (parameters.getBoolean("from_list",false))
        {
            queryIds = new ArrayList<Long>();
            for(CategoryQueryResource q: (List<CategoryQueryResource>)pool.getQueries())
            {
                queryIds.add(q.getIdObject());
                queryObjs.put(q.getIdObject(), q);
            }            
            httpContext.setSessionAttribute(key, queryIds);
        }
        else
        {
            queryIds = (List<Long>)httpContext.getSessionAttribute(key);
            for(Long qId : queryIds)
            {
                try
                {
                    queryObjs.put(qId, coralSession.getStore().getResource(qId));
                }
                catch(EntityDoesNotExistException e)
                {
                    throw new ProcessingException("unexpected exception", e);
                }               
            }
        }
        templatingContext.put("queryIds", queryIds);
        templatingContext.put("queryObjs", queryObjs);
    }
    
    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.pool.modify");
    }
}
