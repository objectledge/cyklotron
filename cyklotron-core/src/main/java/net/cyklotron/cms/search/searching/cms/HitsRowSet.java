package net.cyklotron.cms.search.searching.cms;

import java.util.ArrayList;
import java.util.List;

import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableRow;
import org.objectledge.table.TableState;
import org.objectledge.table.generic.BaseRowSet;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchHit;

/**
 * A <code>TableRowSet</code> implementation which wraps up lucene's search results.
 * This is very important to create a <code>TableTool</code> before closing a <code>Searcher</code>
 * which produced <code>Hits</code> used by this row set, other wise it no field values will be
 * drawn from lucene's index.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsRowSet.java,v 1.9 2007-11-18 21:23:33 rafal Exp $
 */
public class HitsRowSet<T extends SearchHit> extends BaseRowSet<T>
{
    protected TableRow<T>[] rows;
    protected int totalRowCount;

    public HitsRowSet(Context context, List<T> uniqueHits, TableState state,
        SearchHandler<T> searchHandler, LinkTool link, TableFilter<T>[] filters, 
        Subject subject, boolean generateEditLink)
    {
        super(state, filters);
        uniqueHits = filterSearchHits(uniqueHits);
        this.totalRowCount = uniqueHits.size();

        // get rows together with documents contents
        int page = state.getCurrentPage();
        int perPage = state.getPageSize();

        int listSize = totalRowCount;
        int start = 0;
        int end = listSize;

        if(page > 0 && perPage > 0)
        {
            start = (page-1)*perPage;
            end = page*perPage;

            end = ( end<listSize )? end: listSize;
        }
        @SuppressWarnings("unchecked")
        TableRow<T>[] t = new TableRow[end-start]; 
        rows = t;

        for(int i=start, j=0; i<end; i++, j++)
        {
            T hit = uniqueHits.get(i);
            searchHandler.resolveUrls(hit, subject, context, generateEditLink, link);
            rows[j] = new TableRow<T>(Integer.toString(i), hit, 0, 0, 0);
        }
    }
    
    public int getPageRowCount()
    {
        return rows.length;
    }

    public TableRow<T> getParentRow(TableRow<T> childRow)
    {
        return null;
    }

    public TableRow<T> getRootRow()
    {
        return null;
    }

    public TableRow<T>[] getRows()
    {
        return rows;
    }

    @Override
    public TableState getState()
    {
        return state;
    }

    public int getTotalRowCount()
    {
        return totalRowCount;
    }

    public boolean hasMoreChildren(TableRow<T> ancestorRow, TableRow<T> descendantRow)
    {
        return false;
    }
    
    public List<T> filterSearchHits(List<T> uniqueHits)
    {
        List<T> filteredHits = new ArrayList<T>();
        for (int i = 0; i < uniqueHits.size(); i++)
        {
            T hit = uniqueHits.get(i);
            if(accept(hit))
            {
                filteredHits.add(hit);
            }
        }
        return filteredHits;
    }
}