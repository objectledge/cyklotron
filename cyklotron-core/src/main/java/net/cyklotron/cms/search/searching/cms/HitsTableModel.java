package net.cyklotron.cms.search.searching.cms;

import java.util.List;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;
import org.objectledge.web.mvc.tools.LinkTool;

import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchHit;

/**
 * A <code>TableModel</code> implementation which wraps up lucene's search results.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsTableModel.java,v 1.4 2005-06-03 07:29:35 pablo Exp $
 */
public class HitsTableModel<T extends SearchHit> 
    implements TableModel<T>
{
    protected List<T> hits;
    protected HitsRowSet<T> rowSet;
    protected SearchHandler<T> searchHandler;
    protected LinkTool link;
    protected Context context;
    protected Subject subject;
    protected boolean generateEditLink;
    
    public HitsTableModel(Context context, List<T> hits,
        SearchHandler<T> searchHandler,
        LinkTool link, Subject subject, boolean generateEditLink)
    {
        this.context = context;
        this.hits = hits;
        this.searchHandler = searchHandler;
        this.link = link;
        this.subject = subject;
        this.generateEditLink = generateEditLink;
    }

    public TableColumn<T>[] getColumns()
    {
        @SuppressWarnings("unchecked")
        TableColumn<T>[] columns = new TableColumn[6];
        try
        {
            columns[0] = new TableColumn<T>("id", null);
            columns[1] = new TableColumn<T>("modification.time", null);
            columns[2] = new TableColumn<T>("site.name", null);
            columns[3] = new TableColumn<T>("index.title", null);
            columns[4] = new TableColumn<T>("index.abbreviation", null);
            columns[5] = new TableColumn<T>("resource.class.id", null);
        }
        catch(TableException e)
        {
            throw new RuntimeException("Problem creating a column object: "+e.getMessage());
        }
        return columns;
    }
    
    public TableColumn<T> getColumn(String name)
    {
        for(TableColumn<T> column : getColumns())
        {
            if(column.getName().equals(name))
            {
                return column;
            }
        }
        return null;
    }

    public TableRowSet<T> getRowSet(TableState state, TableFilter<T>[] filters)
    {
        if(rowSet == null)
        {
            rowSet = new HitsRowSet<T>(context, hits, state, searchHandler, link, filters, subject, generateEditLink);
            hits = null; // make GC happy
        }
        return rowSet;
    }
}
