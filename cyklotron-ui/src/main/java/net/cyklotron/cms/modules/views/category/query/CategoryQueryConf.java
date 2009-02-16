/*
 * Created on Oct 14, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.category.query;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
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
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Category Query Resutls screen.
 * 
 * @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CategoryQueryConf.java,v 1.4 2005-01-26 06:44:10 pablo Exp $ 
 */
public class CategoryQueryConf 
    extends BaseCMSScreen
{
    protected CategoryQueryService categoryQueryService;

    
    public CategoryQueryConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.categoryQueryService = categoryQueryService;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
       try
        {
            SiteResource site = getSite();
            Resource root = categoryQueryService.getCategoryQueryRoot(coralSession, site);
            Resource[] queries = coralSession.getStore().getResource(root);
            List temp = new ArrayList(queries.length); 
            for(int i = 0; i < queries.length; i++)
            {
                Resource query = queries[i];
                List item = new ArrayList();
                item.add(query.getName());
                item.add(query.getIdString());
                temp.add(item); 
            }
            templatingContext.put("queries", temp);
            CategoryQueryResource query = categoryQueryService.getDefaultQuery(coralSession, site);
            if(query != null)
            {
                templatingContext.put("default_query_id", query.getIdString());
            }
            NavigationNodeResource node = categoryQueryService.getResultsNode(coralSession, site);
            if(node != null)
            {
                templatingContext.put("result_node_path", node.getSitePath());
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.modify");
    }
}
