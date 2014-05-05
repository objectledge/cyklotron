package net.cyklotron.cms.structure.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.objectledge.table.comparator.Direction;

import net.cyklotron.cms.workflow.StateResource;
import net.cyklotron.cms.workflow.StatefulResource;

/**
 * This is a comparator for comparing resources by state.
 * 
 * @author <a href="mailto:lukasz@caltha.pl">Łukasz Urbański</a>
 * @version $Id: StateComparator.java $
 */
public class StateComparator<T extends StatefulResource>
    implements Comparator<T>
{
    private final List<String> stateSortOrderList;

    private final Direction direction;

    public static String[] defaultStateSortOrderList = { "accepted", "assigned", "expired",
                    "locked", "new", "prepared", "published", "rejected", "taken" };

    public StateComparator(Set<String> stateSortOrder, Direction direction)
    {
        this.stateSortOrderList = new ArrayList(stateSortOrder);
        this.direction = direction;
    }

    public StateComparator(Direction direction)
    {
        this.stateSortOrderList = Arrays.asList(defaultStateSortOrderList);
        this.direction = direction;
    }

    public int compare(T r1, T r2)
    {
        StateResource s1 = r1.getState();
        StateResource s2 = r2.getState();

        return Direction.ASC == direction ? stateSortOrderList.indexOf(s1.getName())
            - stateSortOrderList.indexOf(s2.getName()) : stateSortOrderList.indexOf(s2.getName())
            - stateSortOrderList.indexOf(s1.getName());
    }
}
