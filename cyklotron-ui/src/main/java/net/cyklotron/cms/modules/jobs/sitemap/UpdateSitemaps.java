package net.cyklotron.cms.modules.jobs.sitemap;

import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

import net.cyklotron.cms.sitemap.SitemapService;

public class UpdateSitemaps
    implements Valve
{
    private final SitemapService sitemapService;

    public UpdateSitemaps(SitemapService sitemapService)
    {
        this.sitemapService = sitemapService;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        sitemapService.generateSitemaps();
    }
}
