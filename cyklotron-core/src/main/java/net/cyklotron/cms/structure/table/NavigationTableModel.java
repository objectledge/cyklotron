package net.cyklotron.cms.structure.table;

import java.util.Locale;

import net.cyklotron.cms.util.PriorityComparator;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.CoralTableModel;

/**
 * Implementation of Table service based on ARL service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NavigationTableModel.java,v 1.3 2005-01-19 08:23:58 pablo Exp $
 */
public class NavigationTableModel extends CoralTableModel
{
    public NavigationTableModel(CoralSession coralSession, Locale locale)
    {
        super(coralSession, locale);

        // add NavigationResource columns
        comparatorByColumnName.put("sequence", new SequenceComparator());
        comparatorByColumnName.put("title", new TitleComparator(locale));
        comparatorByColumnName.put("validity.start", new ValidityStartComparator());
        comparatorByColumnName.put("validity.end", new ValidityEndComparator());
		comparatorByColumnName.put("priority", new PriorityComparator());
		comparatorByColumnName.put("priority.validity.start", new PriorityAndValidityStartComparator());
    }
}
