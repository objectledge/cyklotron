package net.cyklotron.cms.structure.vote;

/**
 * SortHandler implementation for {@link SortOrder#NEGATIVE_RATIO} sort order.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class NegativeRatioSortHandler
    extends SortHandler<Float>
{
    @Override
    protected Float getValue(int positive, int negative)
    {
        int sum = positive + negative;
        return sum > 0 && negative > 0 ? Float.valueOf((float)negative / (float)sum) : null;
    }
}
