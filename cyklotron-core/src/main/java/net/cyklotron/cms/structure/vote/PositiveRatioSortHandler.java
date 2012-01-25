package net.cyklotron.cms.structure.vote;

/**
 * SortHandler implementation for {@link SortOrder#POSITIVE_RATIO} sort order.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class PositiveRatioSortHandler
    extends SortHandler<Float>
{
    @Override
    protected Float getValue(int positive, int negative)
    {
        int sum = positive + negative;
        return sum > 0 && positive > 0 ? Float.valueOf((float)positive / (float)sum) : null;
    }
}
