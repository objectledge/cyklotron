package net.cyklotron.cms.rewrite;

import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.ProtectedResource;

public interface UrlRewriteRegistry
{
    Set<SitePath> getPaths();

    void drop(SitePath path);

    public Map<String, Map<SitePath, String>> getRewriteInfo();

    SitePath path(Object object);

    ProtectedResource guard(SitePath path);
}
