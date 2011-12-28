package net.cyklotron.cms.forum;

import java.util.Locale;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.coral.table.comparator.CreationTimeComparator;
import org.objectledge.coral.table.comparator.CreatorNameComparator;
import org.objectledge.coral.table.comparator.ModifierNameComparator;
import org.objectledge.coral.table.comparator.OwnerNameComparator;
import org.objectledge.coral.table.comparator.TimeComparator;

import net.cyklotron.cms.util.PriorityComparator;

/**
 * Implementation of Table service based on ARL service
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NavigationTableModel.java,v 1.4 2005-02-09 22:21:01 rafal Exp $
 */
public class MessageTableModel extends CoralTableModel
{
    public MessageTableModel(CoralSession coralSession, Locale locale)
    {
        super(coralSession, locale);

        comparatorByColumnName.put("name", new MessageComparator(MessageComparator.NAME_ASC, locale));
        reverseComparatorByColumnName.put("name", new MessageComparator(MessageComparator.NAME_DESC, locale));
        
        comparatorByColumnName.put("owner.name", new MessageComparator(MessageComparator.OWNER_ASC, locale));
        reverseComparatorByColumnName.put("owner.name", new MessageComparator(MessageComparator.OWNER_DESC, locale));
        
        comparatorByColumnName.put("creator.name", new MessageComparator(MessageComparator.CREATOR_ASC, locale));
        reverseComparatorByColumnName.put("creator.name", new MessageComparator(MessageComparator.CREATOR_DESC, locale));
        
        comparatorByColumnName.put("creation.time", new MessageComparator(MessageComparator.CREATION_DATE_ASC, locale));
        reverseComparatorByColumnName.put("creation.time", new MessageComparator(MessageComparator.CREATION_DATE_DESC, locale));
        
    }
}
