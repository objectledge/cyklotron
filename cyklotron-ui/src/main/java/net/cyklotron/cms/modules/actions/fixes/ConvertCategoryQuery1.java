package net.cyklotron.cms.modules.actions.fixes;

import java.util.HashSet;
import java.util.Set;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.query.CategoryQueryBuilder;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ConvertCategoryQuery1.java,v 1.1 2005-03-29 15:19:49 zwierzem Exp $
 */
public class ConvertCategoryQuery1
    extends BaseCMSAction
{
    private CategoryQueryService categoryQueryService;
    
    public ConvertCategoryQuery1(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService)
    {
        super(logger, structureService, cmsDataFactory);
        
        this.categoryQueryService = categoryQueryService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            QueryResults results = coralSession.getQuery().
                executeQuery("FIND RESOURCE FROM "+CategoryQueryResource.CLASS_NAME);
            Resource[] nodes = results.getArray(1);
            HashSet<CategoryQueryResource> unfixed = new HashSet<CategoryQueryResource>(
                            nodes.length/100+1); 
            for(int i = 0; i < nodes.length; i++)
            {
                CategoryQueryResource res = (CategoryQueryResource)nodes[i];
                if(!fixQuery(coralSession, res))
                {
                    unfixed.add(res);
                }
            }
            templatingContext.put("unfixedQueries", unfixed);
        }
        catch(Exception e)
        {
            logger.error("Structure: CheckNodeState Job Exception",e);            
        }
    }

    private boolean fixQuery(CoralSession coralSession, CategoryQueryResource res)
    {
        if(res.getSimpleQuery(false))
        {
            Set<CategoryResource> requiredSet =
                categoryQueryService.initCategorySelection(coralSession, 
                res.getRequiredCategoryPaths(), "required").keySet();
            Set<CategoryResource> optionalSet =
                categoryQueryService.initCategorySelection(coralSession, 
                res.getOptionalCategoryPaths(), "optional").keySet();
            
            CategoryQueryBuilder queryBuilder = null; 
            try
            {
                queryBuilder = new CategoryQueryBuilder(requiredSet, optionalSet,
                    res.getUseIdsAsIdentifiers(true));
            }
            catch(ProcessingException e)
            {
                logger.error("cannot build category query for query "+res, e);
                return false;
            }
            
            res.setLongQuery(queryBuilder.getQuery());
            res.setRequiredCategoryIdentifiers(
            CategoryQueryUtil.joinCategoryIdentifiers(queryBuilder.getRequiredIdentifiers()));
            res.setOptionalCategoryIdentifiers(
            CategoryQueryUtil.joinCategoryIdentifiers(queryBuilder.getOptionalIdentifiers()));
            res.update();
            
            return true;
        }
        return false;
    }
}
