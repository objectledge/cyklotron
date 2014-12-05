package net.cyklotron.cms.modules.jobs.accesslimits;

import org.objectledge.scheduler.Job;

import net.cyklotron.cms.accesslimits.HitTableManager;

public class SaveHitsTable
    extends Job
{
    private HitTableManager hitTableManager;

    public SaveHitsTable(HitTableManager hitTableManager)
    {
        this.hitTableManager = hitTableManager;
    }

    @Override
    public void run(String[] arguments)
    {
        hitTableManager.save();
    }
}
