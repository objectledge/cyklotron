package net.cyklotron.cms.search.searching.cms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsLinkTool;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchMethod;
import net.cyklotron.cms.search.searching.SearchingException;

/**
 * SearchHandler implementation for searching lucene indexes used by CMS.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LuceneSearchHandler.java,v 1.6 2005-02-09 22:21:34 rafal Exp $
 */
public class LuceneSearchHandler implements SearchHandler
{
    /** search service for getting searchers. */
    private SearchService searchService;
    /** integration service for building URLs for search hits. */
    private IntegrationService integrationService;

    private Context context;
    
    public LuceneSearchHandler(Context context, SearchService searchService,
        IntegrationService integrationService, CmsDataFactory cmsDateFactory)
    {
        this.searchService = searchService;
        this.integrationService = integrationService;
        this.context = context;
    }

    public TableTool search(CoralSession coralSession, Resource[] searchPools, 
        SearchMethod method, TableState state, List tableFilters, 
        Parameters parameters, I18nContext i18nContext)
        throws SearchingException
    {
        Subject subject = coralSession.getUserSubject();
        
        // get the query
        Query query = null;
        try
        {
            query = method.getQuery(coralSession);
        }
        catch(Exception e)
        {
            throw new SearchingException("problem while getting the query", e);
        }

        // setup sorting
        SortField[] sortFields = method.getSortFields();
        Sort sort = null;
        if(sortFields != null)
        {
            sort = new Sort(sortFields);
        }
        
        // get index pools from chosen search pools
        PoolResource[] pools = null;
        ArrayList tmpPools = new ArrayList(searchPools.length);
        for(int i=0; i<searchPools.length; i++)
        {
            Resource pool = searchPools[i];
            if(pool instanceof PoolResource)
            {
                tmpPools.add(pool);
            }
        }
        pools = new PoolResource[tmpPools.size()];
        pools = (PoolResource[])(tmpPools.toArray(pools));
        
        // search
        Searcher searcher = null;
        TableTool tool = null;
        try
        {
            // prepare link tool
            TemplatingContext tContext = (TemplatingContext)
                context.getAttribute(TemplatingContext.class);
            CmsLinkTool link = (CmsLinkTool)tContext.get("link");
            link = (CmsLinkTool)(link.unsetAction().unsetView());

            // perform searching
            searcher = searchService.getSearchingFacility().getSearcher(pools, subject);
            Hits hits = searcher.search(query, sort);
            TableModel model = new HitsTableModel(context, hits, this, link);
            
            tool = new TableTool(state, tableFilters, model);
        }
        catch(SearchException e)
        {
            throw new SearchingException("problem while getting the searcher", e);
        }
        catch(IOException e)
        {
            throw new SearchingException("problem while searching the indexes", e);
        }
        catch(TableException e)
        {
            throw new SearchingException("problem while creating the table tool", e);
        }
        catch(Exception e)
        {
            throw new SearchingException("problem while getting the searcher", e);
        }
        finally
        {
            searchService.getSearchingFacility().returnSearcher(searcher);
        }
        return tool;
    }
    
    ResourceClassResource getHitResourceClassResource(CoralSession coralSession, LuceneSearchHit hit)
    throws EntityDoesNotExistException
    {
        return integrationService.getResourceClass(coralSession,
            coralSession.getSchema().getResourceClass(hit.getResourceClassId()));
    }
}
