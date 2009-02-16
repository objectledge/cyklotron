/*
 * Created on Oct 17, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.category.query;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

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
 
    public CategoryQueryConfUpdate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService)
    {
        super(logger, structureService, cmsDataFactory);
        this.categoryQueryService = categoryQueryService;
    }
        
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException
    {
        SiteResource site = getSite(context);
        try
        {
            long defaultQueryId = parameters.getLong("default_query_id");
            CategoryQueryResource query =
                CategoryQueryResourceImpl.getCategoryQueryResource(coralSession, defaultQueryId);
            categoryQueryService.setDefaultQuery(coralSession, site, query);
            String resultNodePath = parameters.get("result_node_path","");
            NavigationNodeResource resultNode = null;
            if(resultNodePath.length() > 0)
            {
                Resource parent = structureService.getRootNode(coralSession, site).getParent();
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
            categoryQueryService.setResultsNode(coralSession, site, resultNode);
            templatingContext.put("result", "updated_successfully");
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to save settings", e); 
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.modify");
    }
}
