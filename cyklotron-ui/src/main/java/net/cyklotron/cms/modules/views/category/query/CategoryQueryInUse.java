/*
 * Created on Oct 16, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.category.query;

import java.util.ArrayList;

import net.labeo.Labeo;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CategoryQueryInUse extends BaseCMSScreen
{
    protected CategoryQueryService categoryQueryService;
    
    public CategoryQueryInUse()
    {
        categoryQueryService = (CategoryQueryService)Labeo.getBroker().
            getService(CategoryQueryService.SERVICE_NAME);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        CategoryQueryResource query = CategoryQueryUtil.getQuery(coralSession, data);

        SiteResource site = getSite();
        Resource allPools[];
        ArrayList pools = new ArrayList();
        try
        {
            Resource poolRoot = categoryQueryService.getCategoryQueryPoolRoot(site);
            allPools = coralSession.getStore().getResource(poolRoot);
        }
        catch (CategoryQueryException e)
        {
            throw new ProcessingException("failed to lookup query pool root", e);
        }
        for(int i=0; i<allPools.length; i++)
        {
            CategoryQueryPoolResource pool = (CategoryQueryPoolResource)allPools[i];
            if(pool.getQueries().contains(query))
            {
                pools.add(pool);
            }
        }
        templatingContext.put("query", query);
        templatingContext.put("pools", pools);
    }
}
