/*
 * Created on Oct 16, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.category.query;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
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
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
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
    
    
    public CategoryQueryInUse(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.categoryQueryService = categoryQueryService;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CategoryQueryResource query = CategoryQueryUtil.getQuery(coralSession, parameters);

        SiteResource site = getSite();
        Resource allPools[];
        ArrayList pools = new ArrayList();
        try
        {
            Resource poolRoot = categoryQueryService.getCategoryQueryPoolRoot(coralSession, site);
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
