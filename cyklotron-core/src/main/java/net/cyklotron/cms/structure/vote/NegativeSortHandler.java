package net.cyklotron.cms.structure.vote;

/**
 * SortHandler implementation for {@link SortOrder#NEGATIVE} sort order.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class NegativeSortHandler
    extends SortHandler<Integer>
{
    @Override
    protected Integer getValue(int positive, int negative)
    {
        return negative > 0 ? Integer.valueOf(negative) : null;
    }
}
