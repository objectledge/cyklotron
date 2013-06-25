package net.cyklotron.cms.rewrite;

import java.util.Collection;
import java.util.Set;

import net.cyklotron.cms.ProtectedResource;

public interface UrlRewriteRegistry
{
    Set<SitePath> getPaths();

    void drop(SitePath path);

    Collection<RewriteEntry> getRewriteInfo();

    SitePath path(Object object);

    ProtectedResource guard(SitePath path);
}
