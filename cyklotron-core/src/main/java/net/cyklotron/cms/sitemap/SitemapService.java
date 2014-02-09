package net.cyklotron.cms.sitemap;

import java.util.List;

import net.cyklotron.cms.sitemap.internal.SitemapConfiguration;

public interface SitemapService
{
    void generateSitemaps();

    SitemapConfiguration configuration();

    List<SitemapGenerationParticipant> participants();
}
