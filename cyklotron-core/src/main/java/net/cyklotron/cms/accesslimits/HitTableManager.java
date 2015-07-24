package net.cyklotron.cms.accesslimits;

import org.objectledge.web.ratelimit.impl.HitTable;

public interface HitTableManager
{
    HitTable getHitTable();

    void save();

    void archive(String whiteListName);

    void clear(int threshold);
}
