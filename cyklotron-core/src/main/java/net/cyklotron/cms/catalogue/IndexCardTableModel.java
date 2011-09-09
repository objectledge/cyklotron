package net.cyklotron.cms.catalogue;

import java.util.ArrayList;
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
        List<TableColumn<IndexCard>> columns = new ArrayList<TableColumn<IndexCard>>();
        columns.add(initColumn(IndexCard.Property.TITLE, locale));
        columns.add(initColumn(IndexCard.Property.SUBTITLE, locale));
        columns.add(initColumn(IndexCard.Property.KEYWORDS, locale));
        columns.add(initColumn(IndexCard.Property.ABSTRACT, locale));
        columns.add(initColumn(IndexCard.Property.EVENT_TITLE, locale));
        columns.add(initColumn(IndexCard.Property.EVENT_PLACE, locale));
        columns.add(initColumn(IndexCard.Property.EVENT_START, locale));
        columns.add(initColumn(IndexCard.Property.EVENT_END, locale));
        columns.add(initColumn(IndexCard.Property.VALIDITY_START, locale));
        columns.add(initColumn(IndexCard.Property.VALIDITY_END, locale));
        columns.add(initColumn(IndexCard.Property.PUB_YEAR, locale));
        columns.add(initColumn(IndexCard.Property.AUTHORS, locale));
        columns.add(initColumn(IndexCard.Property.SOURCES, locale));
        columns.add(initColumn(IndexCard.Property.ORGANIZATIONS, locale));
        @SuppressWarnings("unchecked")
        TableColumn<IndexCard>[] columnArray = new TableColumn[columns.size()];
        return columns.toArray(columnArray);
    }

    private static TableColumn<IndexCard> initColumn(IndexCard.Property property,
        Locale locale)
        throws TableException
    {
        return new TableColumn<IndexCard>(property.toString(), new IndexCardComparator(property, locale));
    }
}
