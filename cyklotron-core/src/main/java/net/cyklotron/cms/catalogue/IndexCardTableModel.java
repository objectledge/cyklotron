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
        columns.add(initColumn("title", IndexCard.Property.TITLE, locale));
        columns.add(initColumn("subtitle", IndexCard.Property.SUBTITLE, locale));
        columns.add(initColumn("keywords", IndexCard.Property.KEYWORDS, locale));
        columns.add(initColumn("abstract", IndexCard.Property.ABSTRACT, locale));
        columns.add(initColumn("eventTitle", IndexCard.Property.EVENT_TITLE, locale));
        columns.add(initColumn("eventPlace", IndexCard.Property.EVENT_PLACE, locale));
        columns.add(initColumn("eventStart", IndexCard.Property.EVENT_START, locale));
        columns.add(initColumn("eventEnd", IndexCard.Property.EVENT_END, locale));
        columns.add(initColumn("validityStart", IndexCard.Property.VALIDITY_START, locale));
        columns.add(initColumn("validityEnd", IndexCard.Property.VALIDITY_END, locale));
        columns.add(initColumn("pubYear", IndexCard.Property.PUB_YEAR, locale));
        columns.add(initColumn("authors", IndexCard.Property.AUTHORS, locale));
        columns.add(initColumn("sources", IndexCard.Property.SOURCES, locale));
        columns.add(initColumn("organizations", IndexCard.Property.ORGANIZATIONS, locale));
        @SuppressWarnings("unchecked")
        TableColumn<IndexCard>[] columnArray = new TableColumn[columns.size()];
        return columns.toArray(columnArray);
    }

    private static TableColumn<IndexCard> initColumn(String name, IndexCard.Property property,
        Locale locale)
        throws TableException
    {
        return new TableColumn<IndexCard>(name, new IndexCardComparator(property, locale));
    }
}
