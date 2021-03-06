package net.cyklotron.cms.structure.table;

import java.util.Locale;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.coral.table.comparator.TimeComparator;

import net.cyklotron.cms.util.PriorityComparator;

/**
 * Implementation of Table service based on ARL service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NavigationTableModel.java,v 1.4 2005-02-09 22:21:01 rafal Exp $
 */
public class NavigationTableModel extends CoralTableModel
{
    public NavigationTableModel(CoralSession coralSession, Locale locale)
    {
        super(coralSession, locale);

        // add NavigationResource columns
        comparatorByColumnName.put("sequence", new SequenceComparator());
        comparatorByColumnName.put("title", new TitleComparator(locale));
        comparatorByColumnName.put("validity.start", new ValidityStartComparator(
            TimeComparator.Direction.ASC));
        reverseComparatorByColumnName.put("validity.start", new ValidityStartComparator(
            TimeComparator.Direction.DESC));
        comparatorByColumnName.put("validity.end", new ValidityEndComparator(
            TimeComparator.Direction.ASC));
        reverseComparatorByColumnName.put("validity.end", new ValidityEndComparator(
            TimeComparator.Direction.DESC));
        comparatorByColumnName.put("priority", new PriorityComparator());
        comparatorByColumnName.put("priority.validity.start",
            new PriorityAndValidityStartComparator(TimeComparator.Direction.ASC));
        reverseComparatorByColumnName.put("priority.validity.start",
            new PriorityAndValidityStartComparator(TimeComparator.Direction.DESC));
    }
}
