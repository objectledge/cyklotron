/*
 * Created on Oct 14, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.category.query;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Category Query Resutls screen.
 * 
 * @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CategoryQueryConf.java,v 1.3 2005-01-25 11:24:15 pablo Exp $ 
 */
public class CategoryQueryConf 
    extends BaseCMSScreen
{
    protected CategoryQueryService categoryQueryService;

    public CategoryQueryConf()
    {
        categoryQueryService =
            (CategoryQueryService) broker.getService(CategoryQueryService.SERVICE_NAME);
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
       try
        {
            SiteResource site = getSite();
            Resource root = categoryQueryService.getCategoryQueryRoot(site);
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
            CategoryQueryResource query = categoryQueryService.getDefaultQuery(site);
            if(query != null)
            {
                templatingContext.put("default_query_id", query.getIdString());
            }
            NavigationNodeResource node = categoryQueryService.getResultsNode(site);
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
        return checkPermission(context, coralSession, "cms.category.query.modify");
    }
}
