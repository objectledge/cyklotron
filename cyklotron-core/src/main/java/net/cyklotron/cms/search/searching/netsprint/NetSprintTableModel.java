package net.cyklotron.cms.search.searching.netsprint;

import java.io.ByteArrayInputStream;

import org.xml.sax.InputSource;

import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableRowSet;
import net.labeo.services.table.TableState;
import net.labeo.services.xml.XMLService;

/**
 * A <code>TableModel</code> implementation which wraps NetSprint search results.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NetSprintTableModel.java,v 1.1 2005-01-12 20:44:38 pablo Exp $
 */
public class NetSprintTableModel implements TableModel
{
    private NetSprintResultParser parser;
    
    public NetSprintTableModel(byte[] resultsDocument, XMLService xmlService)
    throws Exception
    {
        InputSource is = new InputSource(new ByteArrayInputStream(resultsDocument));
        this.parser = new NetSprintResultParser();
        xmlService.readSAX(is, null, this.parser, this.parser);
    }

    public TableColumn[] getColumns()
    {
        TableColumn[] columns = new TableColumn[4];
        try
        {
            columns[0] = new TableColumn("id", null);
            columns[1] = new TableColumn("modification.time", null);
            columns[2] = new TableColumn("index.title", null);
            columns[3] = new TableColumn("index.abbreviation", null);
        }
        catch(TableException e)
        {
            throw new RuntimeException("Problem creating a column object: "+e.getMessage());
        }
        return columns;
    }

    public TableRowSet getRowSet(TableState state, TableFilter[] filters)
    {
        return new NetSprintRowSet(state, parser, filters);
    }
}
