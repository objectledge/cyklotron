package net.cyklotron.cms.modules.views.fixes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectledge.context.Context;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.query.CategoryQueryBuilder;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.category.query.CategoryResolver;
import net.cyklotron.cms.integration.IntegrationService;

public class ConvertCategoryQueryIdentifiers
    extends BaseCoralView
{
    private CategoryQueryService categoryQueryService;

    private IntegrationService integrationService;

    public ConvertCategoryQueryIdentifiers(Context context,
        CategoryQueryService categoryQueryService, IntegrationService integrationService)
    {
        super(context);
        this.categoryQueryService = categoryQueryService;
        this.integrationService = integrationService;
    }

    @Override
    public void process(Parameters parameters, TemplatingContext templatingContext,
        MVCContext mvcContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Resource[] resources = coralSession.getQuery().executeQuery(
                "FIND RESOURCE FROM category.query").getArray(1);
            CategoryResolver resolver = categoryQueryService.getCategoryResolver(); // added

            Set<CategoryQueryResource> unfixedQueries = new HashSet<CategoryQueryResource>(); // added
            Map<CategoryQueryResource, Set<String>> brokenQueries = new HashMap<CategoryQueryResource, Set<String>>(); // added
            Set<CategoryQueryResource> queryWithDefIdentifiers = new HashSet<CategoryQueryResource>();
            Set<CategoryQueryResource> queryWithoutDefIdentifiers = new HashSet<CategoryQueryResource>();

            for(Resource resource : resources)
            {
                CategoryQueryResource queryResource = (CategoryQueryResource)resource;

                // test query type
                if(queryResource.getSimpleQuery(false)
                    || !queryResource.isLongQueryDefined())
                {
                    // test query for broken categories
                    String[] optCategoryIds = CategoryQueryUtil
                        .splitCategoryIdentifiers(queryResource.getOptionalCategoryIdentifiers());
                    String[] reqCategoryIds = CategoryQueryUtil
                        .splitCategoryIdentifiers(queryResource.getRequiredCategoryIdentifiers());
                    Set<String> actCatIds = new HashSet<String>();
                    Set<String> brokenCatIds = new HashSet<String>();

                    for(String catIds : optCategoryIds)
                    {
                        if(resolver.resolveCategoryIdentifier(catIds) == null)
                        {
                            brokenCatIds.add(catIds);
                        }
                    }
                    for(String catIds : reqCategoryIds)
                    {
                        if(resolver.resolveCategoryIdentifier(catIds) == null)
                        {
                            brokenCatIds.add(catIds);
                        }
                    }

                    // if query has one or more broken categories
                    if(!brokenCatIds.isEmpty())
                    {
                        brokenQueries.put(queryResource, brokenCatIds);
                    }

                    // verify categories identifiers
                    if(queryResource.getUseIdsAsIdentifiers(false))
                    {

                        if(!brokenCatIds.isEmpty())
                        {
                            /*
                             * fix query excluding broken categories
                             */
                            convertQuery(coralSession, queryResource);
                        }
                        queryWithDefIdentifiers.add(queryResource);

                    }
                    else
                    {
                        /*
                         * update query to replace category name path to category id
                         */
                        convertQuery(coralSession, queryResource);
                        queryWithoutDefIdentifiers.add(queryResource);
                    }
                }
                else
                {
                    // if query is long type
                    unfixedQueries.add(queryResource);
                }
            }

            templatingContext.put("unfixed_queries", unfixedQueries);
            templatingContext.put("fixed_queries", brokenQueries);
            templatingContext.put("queries_with_ids", queryWithDefIdentifiers);
            templatingContext.put("queries_without_ids", queryWithoutDefIdentifiers);
        }
        catch(IndexOutOfBoundsException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        catch(MalformedQueryException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }

    private void convertQuery(CoralSession coralSession, CategoryQueryResource queryResource)
        throws ProcessingException
    {
        CategoryQueryResourceData queryData = new CategoryQueryResourceData();
        queryData.init(coralSession, queryResource, categoryQueryService, integrationService);
        CategoryQueryBuilder parsedQuery = new CategoryQueryBuilder(coralSession, queryData
            .getCategoriesSelection(), true);
        queryResource.setUseIdsAsIdentifiers(true);
        queryResource.setQuery(parsedQuery.getQuery());
        queryResource.setRequiredCategoryIdentifiers(CategoryQueryUtil
            .joinCategoryIdentifiers(parsedQuery.getRequiredIdentifiers()));
        queryResource.setOptionalCategoryIdentifiers(CategoryQueryUtil
            .joinCategoryIdentifiers(parsedQuery.getOptionalIdentifiers()));
        queryResource.update();
    }

    /**
     * @{inheritDoc
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role cmsAdministrator = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(cmsAdministrator);
    }
}
