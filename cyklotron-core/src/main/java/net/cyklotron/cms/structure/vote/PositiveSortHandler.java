package net.cyklotron.cms.structure.vote;

/**
 * SortHandler implementation for {@link SortOrder#POSITIVE} sort order.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class PositiveSortHandler
    extends SortHandler<Integer>
{
    @Override
    protected Integer getValue(int positive, int negative)
    {
        return positive > 0 ? Integer.valueOf(positive) : null;
    }
}
