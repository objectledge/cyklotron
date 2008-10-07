package net.cyklotron.cms.util;

import java.util.List;
import java.util.Locale;

import org.objectledge.context.Context;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;

import net.cyklotron.cms.documents.table.EventEndComparator;
import net.cyklotron.cms.documents.table.EventStartComparator;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.table.PriorityAndValidityStartComparator;
import net.cyklotron.cms.structure.table.ValidityStartComparator;

/**
 * Implementation of Table model for CMS resources
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsResourceListTableModel.java,v 1.6 2008-10-07 16:46:43 rafal Exp $
 */
public class CmsResourceListTableModel extends ResourceListTableModel
{
    private IntegrationService integrationService;
    
    private Context context;
    
    public CmsResourceListTableModel(Context context, IntegrationService integrationService,
        Resource[] array, Locale locale)
        throws TableException
    {
        super(array, locale);
        this.integrationService = integrationService;
        this.context = context;
        // this should help with null context and integration service...
        // later should be refactored and done in correct way
        columns = getColumns(locale, array);
    }

    public CmsResourceListTableModel(Context context, IntegrationService integrationService,
        List list, Locale locale)
        throws TableException
    {
        super(list, locale);
        this.integrationService = integrationService;
        this.context = context;
        // this should help with null context and integration service...
        // later should be refactored and done in correct way
        columns = getColumns(locale, (Resource[])list.toArray());
    }

    protected TableColumn[] getColumns(Locale locale, Resource[] array)
        throws TableException
    {
        TableColumn[] cols = super.getColumns(locale, array);
        TableColumn[] newCols = new TableColumn[cols.length + 6];
        for(int i=0; i<cols.length; i++)
        {
            newCols[i] = cols[i];
        }
        newCols[cols.length] = new TableColumn("index.title", new IndexTitleComparator(context, integrationService, locale));
		newCols[cols.length + 1] = new TableColumn("priority", new PriorityComparator());
		newCols[cols.length + 2] = new TableColumn("validity.start", new ValidityStartComparator());
		newCols[cols.length + 3] = new TableColumn("priority.validity.start", new PriorityAndValidityStartComparator());
        newCols[cols.length + 4] = new TableColumn("event.start", new EventStartComparator());
        newCols[cols.length + 5] = new TableColumn("event.end", new EventEndComparator());
        return newCols;
    }
}
