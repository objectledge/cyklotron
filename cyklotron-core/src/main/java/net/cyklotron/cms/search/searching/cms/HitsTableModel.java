package net.cyklotron.cms.search.searching.cms;

import org.apache.lucene.search.Hits;

import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableRowSet;
import net.labeo.services.table.TableState;
import net.labeo.webcore.LinkTool;

/**
 * A <code>TableModel</code> implementation which wraps up lucene's search results.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HitsTableModel.java,v 1.1 2005-01-12 20:45:10 pablo Exp $
 */
public class HitsTableModel implements TableModel
{
    protected Hits hits;
    protected HitsRowSet rowSet;
    protected LuceneSearchHandler searchHandler;
    protected LinkTool link;
    
    public HitsTableModel(Hits hits, LuceneSearchHandler searchHandler, LinkTool link)
    {
        this.hits = hits;
        this.searchHandler = searchHandler;
        this.link = link;
    }

    public TableColumn[] getColumns()
    {
        TableColumn[] columns = new TableColumn[6];
        try
        {
            columns[0] = new TableColumn("id", null);
            columns[1] = new TableColumn("modification.time", null);
            columns[2] = new TableColumn("site.name", null);
            columns[3] = new TableColumn("index.title", null);
            columns[4] = new TableColumn("index.abbreviation", null);
            columns[5] = new TableColumn("resource.class.id", null);
        }
        catch(TableException e)
        {
            throw new RuntimeException("Problem creating a column object: "+e.getMessage());
        }
        return columns;
    }

    public TableRowSet getRowSet(TableState state, TableFilter[] filters)
    {
        if(rowSet == null)
        {
            rowSet = new HitsRowSet(hits, state, searchHandler, link, filters);
            hits = null; // make GC happy
        }
        return rowSet;
    }
}
