package net.cyklotron.cms.search.searching.netsprint;

import net.labeo.services.table.BaseRowSet;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableRow;
import net.labeo.services.table.TableState;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NetSprintRowSet.java,v 1.1 2005-01-12 20:44:38 pablo Exp $
 */
public class NetSprintRowSet
extends BaseRowSet
{
    protected TableRow[] rows;
    protected int totalRowCount;

    public NetSprintRowSet(TableState state, NetSprintResultParser parser, TableFilter[] filters)
    {
        super(state, filters);
        
        ResultsInfo info = parser.getInfo();
        NetSprintSearchHit[] results =  parser.getResults();
        
        totalRowCount = info.getDocumentsFound();
        // WARN: NetSprint limit of displayed results
        totalRowCount = totalRowCount > 500 ? 500: totalRowCount;
        
        // get rows
        rows = new TableRow[results.length];
        for(int i=0; i<rows.length; i++)
        {
            rows[i] = new TableRow(Integer.toString(i), results[i], 0, 0, 0); 
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
}
