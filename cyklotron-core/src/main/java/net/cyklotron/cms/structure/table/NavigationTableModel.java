package net.cyklotron.cms.structure.table;

import java.util.Locale;

import net.labeo.services.resource.table.ARLTableModel;

import net.cyklotron.cms.util.PriorityComparator;

/**
 * Implementation of Table service based on ARL service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NavigationTableModel.java,v 1.1 2005-01-12 20:44:55 pablo Exp $
 */
public class NavigationTableModel extends ARLTableModel
{
    public NavigationTableModel(Locale locale)
    {
        super(locale);

        // add NavigationResource columns
        comparatorByColumnName.put("sequence", new SequenceComparator());
        comparatorByColumnName.put("title", new TitleComparator(locale));
        comparatorByColumnName.put("validity.start", new ValidityStartComparator());
        comparatorByColumnName.put("validity.end", new ValidityEndComparator());
		comparatorByColumnName.put("priority", new PriorityComparator());
		comparatorByColumnName.put("priority.validity.start", new PriorityAndValidityStartComparator());
    }
}
