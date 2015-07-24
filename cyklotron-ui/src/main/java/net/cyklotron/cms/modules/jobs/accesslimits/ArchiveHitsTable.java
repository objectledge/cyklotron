package net.cyklotron.cms.modules.jobs.accesslimits;

import org.objectledge.scheduler.Job;

import net.cyklotron.cms.accesslimits.HitTableManager;

public class ArchiveHitsTable
    extends Job
{
    private HitTableManager hitTableManager;

    public ArchiveHitsTable(HitTableManager hitTableManager)
    {
        this.hitTableManager = hitTableManager;
    }

    @Override
    public void run(String[] arguments)
    {
        hitTableManager.archive();
        hitTableManager.save();
    }
}
