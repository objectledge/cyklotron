package net.cyklotron.cms.structure.vote;
/**
 * SortHandler implementation for {@link SortOrder#TOTAL} sort order.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class TotalSortHander
    extends SortHandler<Integer>
{
    @Override
    protected Integer getValue(int positive, int negative)
    {        
        int sum = positive + negative;
        return sum > 0 ? Integer.valueOf(sum) : null;
    }
}
