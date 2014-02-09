package net.cyklotron.cms.modules.jobs.sitemap;

import org.objectledge.scheduler.Job;

import net.cyklotron.cms.sitemap.SitemapService;

public class UpdateSitemaps
    extends Job
{
    private final SitemapService sitemapService;

    public UpdateSitemaps(SitemapService sitemapService)
    {
        this.sitemapService = sitemapService;
    }

    @Override
    public void run(String[] arguments)
    {
        sitemapService.generateSitemaps();
    }
}
