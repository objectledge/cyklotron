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

/**
 * A <code>TableRowSet</code> implementation which wraps up lucene's search results.
 * This is very important to create a <code>TableTool</code> before closing a <code>Searcher</code>
 * which produced <code>Hits</code> used by this row set, other wise it no field values will be
 * drawn from lucene's index.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsRowSet.java,v 1.9 2007-11-18 21:23:33 rafal Exp $
 */
public class HitsRowSet extends BaseRowSet
{
    protected TableRow[] rows;
    protected int totalRowCount;

    public HitsRowSet(Context context, List<LuceneSearchHit> uniqueHits, TableState state,
        LuceneSearchHandler searchHandler, LinkTool link, TableFilter[] filters, 
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
        rows = new TableRow[end-start];

        for(int i=start, j=0; i<end; i++, j++)
        {
            LuceneSearchHit hit = uniqueHits.get(i);
            try
            {
                    CoralSession coralSession = context.getAttribute(CoralSession.class);
                    ResourceClassResource rcr = searchHandler.getHitResourceClassResource(coralSession, hit);
                    hit.setUrl(link.view(rcr.getView()).set("res_id", hit.getId()).toString());
                    if(generateEditLink)
                    {
                        Resource resource = searchHandler.getHitResource(coralSession, hit);
                        if(resource != null)
                        {
                            if(!(resource instanceof ProtectedResource) || 
                                ((ProtectedResource)resource).canModify(coralSession, subject))
                            {
                                if(rcr.getEditView() != null)
                                {
                                    hit.setEditUrl(link.view(rcr.getEditView()).set("res_id", hit.getId()).toString());
                                }
                            }
                        }
                    }
            }
            catch(EntityDoesNotExistException e)
            {
                    // could not retrieve ResourceClass - leave empty URL
            }
            rows[j] = new TableRow(Integer.toString(i), hit, 0, 0, 0);
        }
    }
    
    public int getPageRowCount()
    {
        return rows.length;
    }

    public TableRow getParentRow(TableRow childRow)
    {
        return null;
    }

    public TableRow getRootRow()
    {
        return null;
    }

    public TableRow[] getRows()
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

    public boolean hasMoreChildren(TableRow ancestorRow, TableRow descendantRow)
    {
        return false;
    }
    
    public List<LuceneSearchHit> filterSearchHits(List<LuceneSearchHit> uniqueHits)
    {
        List<LuceneSearchHit> filteredHits = new ArrayList<LuceneSearchHit>();
        for (int i = 0; i < uniqueHits.size(); i++)
        {
            LuceneSearchHit hit = uniqueHits.get(i);
            if(accept(hit))
            {
                filteredHits.add(hit);
            }
        }
        return filteredHits;
    }
}