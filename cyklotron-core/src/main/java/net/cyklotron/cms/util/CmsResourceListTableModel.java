package net.cyklotron.cms.util;

import java.util.List;
import java.util.Locale;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ResourceListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableException;

import net.cyklotron.cms.documents.table.EventEndComparator;
import net.cyklotron.cms.documents.table.EventStartComparator;
import net.cyklotron.cms.structure.table.PriorityAndValidityStartComparator;
import net.cyklotron.cms.structure.table.ValidityStartComparator;

/**
 * Implementation of Table model for CMS resources
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsResourceListTableModel.java,v 1.1 2005-01-12 20:44:32 pablo Exp $
 */
public class CmsResourceListTableModel extends ResourceListTableModel
{
    public CmsResourceListTableModel(Resource[] array, Locale locale)
        throws TableException
    {
        super(array, locale);
    }

    public CmsResourceListTableModel(List list, Locale locale)
        throws TableException
    {
        super(list, locale);
    }

    protected TableColumn[] getColumns(Locale locale)
        throws TableException
    {
        TableColumn[] cols = super.getColumns(locale);
        TableColumn[] newCols = new TableColumn[cols.length + 6];
        for(int i=0; i<cols.length; i++)
        {
            newCols[i] = cols[i];
        }
        newCols[cols.length] = new TableColumn("index.title", new IndexTitleComparator(locale));
		newCols[cols.length + 1] = new TableColumn("priority", new PriorityComparator());
		newCols[cols.length + 2] = new TableColumn("validity.start", new ValidityStartComparator());
		newCols[cols.length + 3] = new TableColumn("priority.validity.start", new PriorityAndValidityStartComparator());
        newCols[cols.length + 4] = new TableColumn("event.start", new EventStartComparator());
        newCols[cols.length + 5] = new TableColumn("event.end", new EventEndComparator());
        return newCols;
    }
}
