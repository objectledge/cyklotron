package net.cyklotron.cms.modules.jobs.accesslimits;

import org.objectledge.scheduler.Job;

import net.cyklotron.cms.accesslimits.HitTableManager;

public class ClearHitsTable
    extends Job
{
    private HitTableManager hitTableManager;

    public ClearHitsTable(HitTableManager hitTableManager)
    {
        this.hitTableManager = hitTableManager;
    }

    @Override
    public void run(String[] arguments)
    {
        int threshold = arguments.length > 0 ? Integer.parseInt(arguments[0]) : Integer.MAX_VALUE;
        hitTableManager.clear(threshold);
        hitTableManager.save();
    }
}
