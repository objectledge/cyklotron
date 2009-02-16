package net.cyklotron.cms.modules.views.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryBuilder;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.forum.CommentaryResource;
import net.cyklotron.cms.forum.DiscussionResource;
import net.cyklotron.cms.forum.ForumResource;
import net.cyklotron.cms.forum.MessageResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.category.CategoryList;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.table.ValidityStartFilter;

/**
 * CMS Statistics main screen.
 *
 */
public class ForumStatistics extends CategoryList
{
    private CategoryQueryService categoryQueryService;
    
    private UserManager userManager;
    
    public ForumStatistics(org.objectledge.context.Context context, Logger logger,
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
    
    public void process(Parameters parameters, MVCContext mvcContext, 
        TemplatingContext templatingContext, HttpContext httpContext, 
        I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        SimpleDateFormat df = new SimpleDateFormat(DateAttributeHandler.DATE_TIME_FORMAT);
        SiteResource site = getSite();
        //      categories
        CategoryQueryResourceData queryData = CategoryQueryResourceData.getData(httpContext, null);
        templatingContext.put("query_data", queryData);
        Set expandedCategoriesIds = new HashSet();
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
        
        Date validityStart = null;
        Date validityEnd = null;
        Date createdStart = null;
        Date createdEnd = null;
        Subject creator = null;

        // prepare the conditions...
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
        ValidityStartFilter validityStartFilter = new ValidityStartFilter(validityStart, validityEnd);
        CreationTimeFilter creationTimeFilter = new CreationTimeFilter(createdStart, createdEnd);

        String createdBy = parameters.get("created_by","");
        String type = parameters.get("type","");
        templatingContext.put("type", type);

        int counter = 0;
        try
        {
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
        StringBuilder sb = new StringBuilder("");
        boolean discussionQuery = false;
        boolean commentsQuery = false;
        HashSet<Long> catResourcesIds = new HashSet<Long>();
        String catQuery = parsedQuery.getQuery();
        if((type.equals("cPost") || type.equals("comment")) && 
            catQuery != null && catQuery.length() > 0)
        {
            commentsQuery = true;
            try
            {
                Resource[] docs = categoryQueryService.forwardQuery(coralSession, catQuery);
                for(Resource doc: docs)
                {
                    catResourcesIds.add(doc.getId());
                }
            }
            catch(Exception e)
            {
                throw new ProcessingException("failed to execute category query", e);
            }
        }
        if(type.equals("cPost")||type.equals("dPost"))
        {
            sb.append("FIND RESOURCE FROM cms.forum.message");    
        }
        else
        {
            discussionQuery = true;
            sb.append("FIND RESOURCE FROM cms.forum.discussion");
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
            HashSet<DiscussionResource> discussions = new HashSet<DiscussionResource>();
            HashSet<DiscussionResource> comments = new HashSet<DiscussionResource>();
            HashSet<DiscussionResource> catComments = new HashSet<DiscussionResource>();
            int dMessages = 0;
            int cMessages = 0;
            int catMessages = 0;
            QueryResults results = coralSession.getQuery().executeQuery(query);
            Resource[] nodes = results.getArray(1);
            for (int i = 0; i < nodes.length; i++)
            {
                if(!discussionQuery)
                {
                    MessageResource message = (MessageResource)nodes[i];
                    DiscussionResource discussion = message.getDiscussion();
                    ForumResource forum = discussion.getForum();
                    SiteResource fsite = forum.getSite();
                    if(fsite.equals(site))
                    {
                        if(discussion instanceof CommentaryResource)
                        {
                            if(commentsQuery)
                            {
                                // check whether commentary assigned to categorized doc
                                long id = ((CommentaryResource)discussion).getResourceId();
                                if(catResourcesIds.contains(id))
                                {
                                    comments.add(discussion);
                                    cMessages++;
                                }
                            }
                            else
                            {
                                comments.add(discussion);
                                cMessages++;
                            }
                        }
                        else
                        {
                            discussions.add(discussion);
                            dMessages++;
                        }
                    }
                }
                else
                {
                    DiscussionResource discussion = (DiscussionResource)nodes[i];
                    ForumResource forum = discussion.getForum();
                    SiteResource fsite = forum.getSite();
                    if(fsite.equals(site))
                    {
                        if(type.equals("comment"))
                        {
                            if(discussion instanceof CommentaryResource)
                            {
                                if(commentsQuery)
                                {
                                    // check whether commentary assigned to categorized doc
                                    long id = ((CommentaryResource)discussion).getResourceId();
                                    if(catResourcesIds.contains(id))
                                    {
                                        comments.add(discussion);
                                        cMessages++;
                                    }
                                }
                                else
                                {
                                    comments.add(discussion);
                                }
                            }
                        }
                        else
                        {
                            if(!(discussion instanceof CommentaryResource))
                            {
                                discussions.add(discussion);
                            }
                        }
                    }
                }
            }
            templatingContext.put("discussions", discussions);
            templatingContext.put("comments", comments);
            templatingContext.put("catComments", catComments);
            templatingContext.put("dMessages", dMessages);
            templatingContext.put("cMessages", cMessages);
            templatingContext.put("catMessages", catMessages);
        }
        catch (Exception e)
        {
            throw new ProcessingException("Exception occured during query execution", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}