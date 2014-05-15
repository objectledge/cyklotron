package net.cyklotron.cms.structure.table;

import java.util.Locale;

import org.objectledge.table.comparator.BaseStringComparator;
import org.objectledge.table.comparator.Direction;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * This is a comparator for comparing navigation resource titles.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: TitleComparator.java,v 1.4 2005-02-15 17:31:58 rafal Exp $
 */
public class TitleComparator<T extends NavigationNodeResource>
    extends BaseStringComparator<T>
{
    private final Direction direction;

    public TitleComparator(Locale locale)
    {
        super(locale);
        this.direction = Direction.ASC;
    }
    
    public TitleComparator(Locale locale, Direction direction)
    {
        super(locale);
        this.direction = direction;
    }

    public int compare(T r1, T r2)
    {
        return Direction.ASC == direction ? compareStrings(r1.getTitle(), r2.getTitle())
            : compareStrings(r2.getTitle(), r1.getTitle());
    }
}
