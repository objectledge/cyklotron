package net.cyklotron.cms.search.searching.cms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.CmsLinkTool;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchMethod;
import net.cyklotron.cms.search.searching.SearchingException;
import net.labeo.services.ServiceBroker;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

/**
 * SearchHandler implementation for searching lucene indexes used by CMS.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LuceneSearchHandler.java,v 1.2 2005-01-18 17:38:26 pablo Exp $
 */
public class LuceneSearchHandler implements SearchHandler
{
    /** search service for getting searchers. */
    private SearchService searchService;
    /** resource service for building URLs for search hits. */
    private CoralSession resourceService;
    /** integration service for building URLs for search hits. */
    private IntegrationService integrationService;

    public LuceneSearchHandler(SearchService searchService, CoralSession resourceService,
        IntegrationService integrationService)
    {
        this.searchService = searchService;
        this.resourceService = resourceService;
        this.integrationService = integrationService;
    }

    public TableTool search(Resource[] searchPools, SearchMethod method, TableState state, List tableFilters, RunData data)
        throws SearchingException
    {
        Subject subject = CmsTool.getSubject(data);
        
        // get the query
        Query query = null;
        try
        {
            query = method.getQuery();
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
            CmsLinkTool link = (CmsLinkTool)data.getLinkTool();
            link = (CmsLinkTool)(link.unsetAction().app("cms").unsetView());

            // perform searching
            searcher = searchService.getSearchingFacility().getSearcher(pools, subject);
            Hits hits = searcher.search(query, sort);
            TableModel model = new HitsTableModel(hits, this, link);
            
            tool = new TableTool(state, model, tableFilters);
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
    
    ResourceClassResource getHitResourceClassResource(LuceneSearchHit hit)
    throws EntityDoesNotExistException
    {
        return integrationService.getResourceClass(
            resourceService.getSchema().getResourceClass(hit.getResourceClassId()));
    }
}
