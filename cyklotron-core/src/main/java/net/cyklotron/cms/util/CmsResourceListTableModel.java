package net.cyklotron.cms.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.cyklotron.cms.PrioritizedResource;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.table.EventEndComparator;
import net.cyklotron.cms.documents.table.EventStartComparator;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.table.PriorityAndValidityStartComparator;
import net.cyklotron.cms.structure.table.StateComparator;
import net.cyklotron.cms.structure.table.TitleComparator;
import net.cyklotron.cms.structure.table.ValidityStartComparator;

import org.objectledge.context.Context;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.coral.table.comparator.TimeComparator;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.comparator.Direction;

/**
 * Implementation of Table model for CMS resources
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CmsResourceListTableModel.java,v 1.7 2008-10-21 14:44:18 rafal Exp $
 */
public class CmsResourceListTableModel<T extends Resource> extends ResourceListTableModel<T>
{
    private IntegrationService integrationService;
    
    private Context context;
    
    public CmsResourceListTableModel(Context context, IntegrationService integrationService,
        T[] array, Locale locale)
        throws TableException
    {
        this(context, integrationService, Arrays.asList(array), locale);
    }

    public CmsResourceListTableModel(Context context, IntegrationService integrationService,
        List<T> list, Locale locale)
        throws TableException
    {
        super(list, locale);
        this.integrationService = integrationService;
        this.context = context;
        // this should help with null context and integration service...
        // later should be refactored and done in correct way
        columns = getColumns(locale, list);
    }

    protected TableColumn<T>[] getColumns(Locale locale, List<T> list)
        throws TableException
    {
        TableColumn<T>[] cols = super.getColumns(locale, list);
        TableColumn<?>[] newCols = new TableColumn[cols.length + 8];
        for(int i=0; i<cols.length; i++)
        {
            newCols[i] = cols[i];
        }
        newCols[cols.length] = new TableColumn<T>("index.title", new IndexTitleComparator<T>(context, integrationService, locale));
		newCols[cols.length + 1] = new TableColumn<PrioritizedResource>("priority", new PriorityComparator<PrioritizedResource>());
        newCols[cols.length + 2] = new TableColumn<NavigationNodeResource>("validity.start",
            new ValidityStartComparator(TimeComparator.Direction.ASC),
            new ValidityStartComparator(TimeComparator.Direction.DESC));
        newCols[cols.length + 3] = new TableColumn<NavigationNodeResource>("priority.validity.start",
            new PriorityAndValidityStartComparator(TimeComparator.Direction.ASC),
            new PriorityAndValidityStartComparator(TimeComparator.Direction.DESC));
        newCols[cols.length + 4] = new TableColumn<DocumentNodeResource>("event.start",
            new EventStartComparator(TimeComparator.Direction.ASC), new EventStartComparator(
                TimeComparator.Direction.DESC));
        newCols[cols.length + 5] = new TableColumn<DocumentNodeResource>("event.end", new EventEndComparator(
            TimeComparator.Direction.ASC), new EventEndComparator(TimeComparator.Direction.DESC));
        newCols[cols.length + 6] = new TableColumn<NavigationNodeResource>("title",
            new TitleComparator(locale, Direction.ASC), new TitleComparator(locale, Direction.DESC));
        newCols[cols.length + 7] = new TableColumn<NavigationNodeResource>("state",
            new StateComparator(Direction.ASC), new StateComparator(Direction.DESC));
        return (TableColumn<T>[])newCols;
    }
}
