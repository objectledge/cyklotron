/*
 * Created on Oct 17, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.category.query;

import net.labeo.Labeo;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CategoryQueryConfUpdate 
    extends BaseCMSAction 
{
    private CategoryQueryService categoryQueryService;
        
    public CategoryQueryConfUpdate()
    {
        categoryQueryService = (CategoryQueryService)Labeo.getBroker().
            getService(CategoryQueryService.SERVICE_NAME);
    }
        
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException, NotFoundException
    {
        SiteResource site = getSite(context);
        Context context = data.getContext();
        try
        {
            long defaultQueryId = parameters.getLong("default_query_id");
            CategoryQueryResource query =
                CategoryQueryResourceImpl.getCategoryQueryResource(coralSession, defaultQueryId);
            categoryQueryService.setDefaultQuery(site, query);
            String resultNodePath = parameters.get("result_node_path","");
            NavigationNodeResource resultNode = null;
            if(resultNodePath.length() > 0)
            {
                Resource parent = structureService.getRootNode(site).getParent();
                Resource[] res = coralSession.getStore().getResourceByPath(parent.getPath()+resultNodePath);
                if(res.length == 1)
                {
                    resultNode = (NavigationNodeResource)res[0];
                }
                else if(res.length > 1)
                {
                    throw new ProcessingException("multiple nodes with path "+resultNodePath);
                }
                else
                {
                    templatingContext.put("result", "no_node_with_given_path");
                    return;
                }
            }
            categoryQueryService.setResultsNode(site, resultNode);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to save settings", e); 
        }
    }
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.query.modify");
    }
}
