package net.cyklotron.cms.accesslimits;

import org.objectledge.web.ratelimit.impl.HitTable;

/**
 * WIP in-memory hit table.
 */
public class HitTableManagerImpl
    implements HitTableManager
{
    private final HitTable hitTable;

    public HitTableManagerImpl()
    {
        hitTable = new HitTable();
    }

    @Override
    public HitTable getHitTable()
    {
        return hitTable;
    }
}
