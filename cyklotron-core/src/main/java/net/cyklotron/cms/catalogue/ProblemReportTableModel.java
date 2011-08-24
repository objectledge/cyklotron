package net.cyklotron.cms.catalogue;

import java.util.List;
import java.util.Locale;

import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.generic.ListTableModel;

public class ProblemReportTableModel
    extends ListTableModel<ProblemReportItem>
{

    public ProblemReportTableModel(List<ProblemReportItem> list, Locale locale)
        throws TableException
    {
        super(list, initColumns(locale));
    }

    private static TableColumn<ProblemReportItem>[] initColumns(Locale locale)
        throws TableException
    {
        @SuppressWarnings("unchecked")
        TableColumn<ProblemReportItem>[] columns = new TableColumn[1];
        columns[0] = new TableColumn<ProblemReportItem>("resource",
            new ProblemReportItemComparator(locale));
        return columns;
    }

}
