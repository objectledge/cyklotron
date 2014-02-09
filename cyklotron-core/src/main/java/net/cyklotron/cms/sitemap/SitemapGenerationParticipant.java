package net.cyklotron.cms.sitemap;

import java.util.Iterator;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.site.SiteResource;

public interface SitemapGenerationParticipant
{
    String name();

    boolean supportsConfiguration();

    Iterator<SitemapItem> items(SiteResource site, String domain, Parameters parameters,
        CoralSession coralSession);
}
