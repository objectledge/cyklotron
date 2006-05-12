package net.cyklotron.cms.modules.views.statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.DateAttributeHandler;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.coral.table.filter.CreationTimeFilter;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryBuilder;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.category.CategoryList;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.table.ValidityStartFilter;

/**
 * CMS Statistics main screen.
 *
 */
public class Statistics extends CategoryList
{
    private CategoryQueryService categoryQueryService;
    
    private UserManager userManager;
    
    public Statistics(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService,
        CategoryQueryService categoryQueryService, UserManager userManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService);
        this.categoryQueryService = categoryQueryService;
        this.userManager = userManager;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        SimpleDateFormat df = new SimpleDateFormat(DateAttributeHandler.DATE_TIME_FORMAT);
        Resource[] states = coralSession.getStore().getResourceByPath("/cms/workflow/automata/structure.navigation_node/states/*");
        templatingContext.put("states", states);

        SiteResource site = getSite();
        //      categories
        CategoryQueryResourceData queryData = CategoryQueryResourceData.getData(httpContext, null);
        templatingContext.put("query_data", queryData);
        Set<Long> expandedCategoriesIds = new HashSet<Long>();
        // setup pool data and table data
        if (queryData.isNew())
        {
            queryData.init(coralSession, null, categoryQueryService, integrationService);
            // prepare expanded categories - includes inherited ones
            Map initialState = queryData.getCategoriesSelection().getEntities(coralSession);
            for(Iterator i=initialState.keySet().iterator(); i.hasNext();)
            {
                CategoryResource category = (CategoryResource)(i.next());
                CategoryResource[] cats = categoryService.getImpliedCategories(category, true);
                for(int j=0; j<cats.length; j++)
                {
                    expandedCategoriesIds.add(cats[j].getIdObject());
                }
            }
        }
        else
        {
            queryData.update(parameters);
        }
        
        // categories
        prepareGlobalCategoriesTableTool(coralSession, templatingContext, i18nContext
            , expandedCategoriesIds, false);
        prepareSiteCategoriesTableTool(coralSession, templatingContext, i18nContext, expandedCategoriesIds, site, false);
        templatingContext.put("category_tool", new CategoryInfoTool(context,integrationService, categoryService));
        
        if (parameters.get("show","").length() == 0)
        {
            return;
        }

        CategoryQueryBuilder parsedQuery = new CategoryQueryBuilder(coralSession, 
            queryData.getCategoriesSelection(), queryData.useIdsAsIdentifiers());
        templatingContext.put("parsed_query", parsedQuery);
        
        Resource state = null;
        Date validityStart = null;
        Date validityEnd = null;
        Date createdStart = null;
        Date createdEnd = null;
        Subject creator = null;

        // prepare the conditions...
        if (parameters.get("validity_start","").length() > 0)
        {
            validityStart = new Date(parameters.getLong("validity_start"));
            templatingContext.put("validity_start", validityStart);
        }
        if (parameters.get("validity_end","").length() > 0)
        {
            validityEnd = new Date(parameters.getLong("validity_end"));
            templatingContext.put("validity_end", validityEnd);
        }
        if (parameters.get("created_start","").length() > 0)
        {
            createdStart = new Date(parameters.getLong("created_start"));
            templatingContext.put("created_start", createdStart);
        }
        if (parameters.get("created_end","").length() > 0)
        {
            createdEnd = new Date(parameters.getLong("created_end"));
            templatingContext.put("created_end", createdEnd);
        }
        String createdBy = parameters.get("created_by","");
        long stateId = parameters.getLong("selected_state", -1);
        boolean selectedCategory = false;
        HashSet<Resource> fromCategorySet = new HashSet<Resource>();
        int counter = 0;
        try
        {
            if (stateId != -1)
            {
                state = coralSession.getStore().getResource(stateId);
                templatingContext.put("selected_state", state);
            }
            String catQuery = parsedQuery.getQuery();
            if(catQuery != null && catQuery.length() > 0)
            {
                selectedCategory = true;
                try
                {
                    Resource[] docs = categoryQueryService.forwardQuery(coralSession, catQuery);
                    for(Resource doc: docs)
                    {
                        fromCategorySet.add(doc);
                    }
                }
                catch(Exception e)
                {
                    throw new ProcessingException("failed to execute category query", e);
                }
            }
            
            
            
            /**
            if (parameters.get("category_id","").length() > 0)
            {
                long categoryId = parameters.getLong("category_id", -1);
                category = CategoryResourceImpl.getCategoryResource(coralSession, categoryId);
                templatingContext.put("category", category);
            }
            */
            
            if (createdBy.length() > 0)
            {
                try
                {
                    String dn = userManager.getUserByLogin(createdBy).getName();
                    creator = coralSession.getSecurity().getSubject(dn);
                    templatingContext.put("created_by", createdBy);
                }
                catch (Exception e)
                {
                    // do nothing...or maybe report that user is unknown!
                    templatingContext.put("result", "unknown_user");
                }
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("Exception occured during query preparation");
        }

        boolean nextCondition = false;
        StringBuilder sb = new StringBuilder("FIND RESOURCE FROM documents.document_node");
        if (site != null)
        {
            nextCondition = true;
            sb.append(" WHERE site = ");
            sb.append(site.getIdString());
        }

        if (state != null)
        {
            if (nextCondition)
            {
                sb.append(" AND ");
            }
            else
            {
                sb.append(" WHERE ");
            }
            sb.append("state = " + state.getIdString());
            nextCondition = true;
        }

        if (creator != null)
        {
            if (nextCondition)
            {
                sb.append(" AND ");
            }
            else
            {
                sb.append(" WHERE ");
            }
            sb.append("created_by = " + creator.getIdString());
            nextCondition = true;
        }

        if (validityStart != null)
        {
            if (nextCondition)
            {
                sb.append(" AND ");
            }
            else
            {
                sb.append(" WHERE ");
            }
            sb.append("validityStart > '" + df.format(validityStart) + "'");
            nextCondition = true;
        }
        if (validityEnd != null)
        {
            if (nextCondition)
            {
                sb.append(" AND ");
            }
            else
            {
                sb.append(" WHERE ");
            }
            sb.append("validityStart < '" + df.format(validityEnd) + "'");
            nextCondition = true;
        }
        if (createdStart != null)
        {
            if (nextCondition)
            {
                sb.append(" AND ");
            }
            else
            {
                sb.append(" WHERE ");
            }
            sb.append("creation_time > '" + df.format(createdStart) + "'");
            nextCondition = true;
        }
        if (createdEnd != null)
        {
            if (nextCondition)
            {
                sb.append(" AND ");
            }
            else
            {
                sb.append(" WHERE ");
            }
            sb.append("creation_time < '" + df.format(createdEnd) + "'");
            nextCondition = true;
        }
        String query = sb.toString();
        templatingContext.put("query", query);
        try
        {
            QueryResults results = coralSession.getQuery().executeQuery(query);
            Resource[] nodes = results.getArray(1);
            HashMap<Subject, Integer> editors = new HashMap<Subject, Integer>();
            HashMap<Subject, Integer> redactors = new HashMap<Subject, Integer>();
            HashMap<Subject, Integer> creators = new HashMap<Subject, Integer>();
            HashMap<Subject, Integer> acceptors = new HashMap<Subject, Integer>();
            if(!selectedCategory)
            {
                counter = nodes.length;
                if (site != null)
                {
                    for (int i = 0; i < nodes.length; i++)
                    {
                        countRedactors(nodes[i], redactors, editors, creators, acceptors);
                    }
                }
            }
            else
            {
                counter = 0;
                for (int i = 0; i < nodes.length; i++)
                {
                    if (fromCategorySet.contains(nodes[i]))
                    {
                        counter++;
                        countRedactors(nodes[i], redactors, editors, creators, acceptors);
                    }
                }
            }
            templatingContext.put("counter", new Integer(counter));


            if (site != null)
            {
                templatingContext.put("editors", editors);
                templatingContext.put("redactors", redactors);
                templatingContext.put("creators", creators);
                templatingContext.put("acceptors", acceptors);
                HashSet<Subject> users = new HashSet<Subject>();
                Iterator<Subject> it = editors.keySet().iterator();
                while (it.hasNext())
                {
                    users.add(it.next());
                }
                it = redactors.keySet().iterator();
                while (it.hasNext())
                {
                    users.add(it.next());
                }
                it = creators.keySet().iterator();
                while (it.hasNext())
                {
                    users.add(it.next());
                }
                it = acceptors.keySet().iterator();
                while (it.hasNext())
                {
                    users.add(it.next());
                }
                ArrayList<Subject> usersList = new ArrayList<Subject>(users);
                Collections.sort(usersList, new NameComparator(i18nContext.getLocale()));
                templatingContext.put("users", usersList);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("Exception occured during query execution", e);
        }
    }

    private void countRedactors(Resource resource, Map<Subject, Integer> redactors, 
			Map<Subject, Integer> editors, Map<Subject, Integer> creators, Map<Subject, Integer>acceptors)
    {
        NavigationNodeResource node = (NavigationNodeResource)resource;
        Subject redactor = node.getOwner();
        Subject editor = node.getLastEditor();
        Subject creator = node.getCreatedBy();
        Subject acceptor = node.getLastAcceptor();
        if (redactor != null)
        {
            Integer redactorCount = (Integer)redactors.get(redactor);
            if (redactorCount == null)
            {
                redactorCount = new Integer(1);
                redactors.put(redactor, redactorCount);
            }
            else
            {
                redactors.put(redactor, new Integer(redactorCount.intValue() + 1));
            }
        }
        if (acceptor != null)
        {
            Integer acceptorCount = (Integer)acceptors.get(acceptor);
            if (acceptorCount == null)
            {
                acceptorCount = new Integer(1);
                acceptors.put(acceptor, acceptorCount);
            }
            else
            {
                acceptors.put(acceptor, new Integer(acceptorCount.intValue() + 1));
            }
        }
        if (editor != null)
        {
            Integer editorCount = (Integer)editors.get(editor);
            if (editorCount == null)
            {
                editorCount = new Integer(1);
                editors.put(editor, editorCount);
            }
            else
            {
                editors.put(editor, new Integer(editorCount.intValue() + 1));
            }
        }
        if (creator != null)
        {
            Integer creatorCount = (Integer)creators.get(creator);
            if (creatorCount == null)
            {
                creatorCount = new Integer(1);
                creators.put(creator, creatorCount);
            }
            else
            {
                creators.put(creator, new Integer(creatorCount.intValue() + 1));
            }
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
                SiteResource site = getSite();
                Role role = null;
                if(site != null)
                {
                    role = site.getAdministrator();
                }
                else
                {
                    role = coralSession.getSecurity().getUniqueRole("cms.administrator");
                }
                return coralSession.getUserSubject().hasRole(role);
        }
        catch(ProcessingException e)
        {
            logger.error("Subject has no rights to view this screen",e);
            return false;
        }
    }
}