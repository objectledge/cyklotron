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
    public void run(String[] in)
    {
        String[] args = in != null && in.length > 0 && in[0] != null ? in[0].split(" ")
            : new String[0];
        int threshold = args.length > 0 ? Integer.parseInt(args[0]) : Integer.MAX_VALUE;
        String whiteListName = args.length > 1 ? args[1] : null;
        hitTableManager.archive(threshold, whiteListName);
        hitTableManager.save();
    }
}
