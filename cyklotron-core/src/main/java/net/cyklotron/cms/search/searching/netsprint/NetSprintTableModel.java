package net.cyklotron.cms.search.searching.netsprint;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;
import org.xml.sax.InputSource;

/**
 * A <code>TableModel</code> implementation which wraps NetSprint search results.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NetSprintTableModel.java,v 1.2 2005-01-20 06:52:43 pablo Exp $
 */
public class NetSprintTableModel implements TableModel
{
    private NetSprintResultParser parser;
    
    public NetSprintTableModel(byte[] resultsDocument)
    throws Exception
    {
        InputSource is = new InputSource(new ByteArrayInputStream(resultsDocument));
        // handler
        this.parser = new NetSprintResultParser();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(is, parser);
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
