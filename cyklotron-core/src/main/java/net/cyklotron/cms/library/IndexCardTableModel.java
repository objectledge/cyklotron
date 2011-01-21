package net.cyklotron.cms.library;

import java.util.List;
import java.util.Locale;

import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.generic.ListTableModel;

public class IndexCardTableModel
    extends ListTableModel<IndexCard>
{
    public IndexCardTableModel(List<IndexCard> cards, Locale locale)
        throws TableException
    {
        super(cards, initColumns(locale));
    }

    private static TableColumn<IndexCard>[] initColumns(Locale locale)
        throws TableException
    {
        @SuppressWarnings("unchecked")
        TableColumn<IndexCard>[] columns = new TableColumn[4];
        columns[0] = new TableColumn<IndexCard>("title", new IndexCardComparator(
            IndexCard.Property.TITLE, locale));
        columns[1] = new TableColumn<IndexCard>("authors", new IndexCardComparator(
            IndexCard.Property.AUTHORS, locale));
        columns[2] = new TableColumn<IndexCard>("pubYear", new IndexCardComparator(
            IndexCard.Property.PUB_YEAR, locale));
        columns[3] = new TableColumn<IndexCard>("keywords", new IndexCardComparator(
            IndexCard.Property.KEYWORDS, locale));
        return columns;
    }
}
