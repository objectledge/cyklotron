package net.cyklotron.cms.rewrite;

import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.ProtectedResource;

public interface UrlRewriteRegistry
{
    Set<String> getPaths();

    void drop(String path);

    public Map<String, Map<String, String>> getRewriteInfo();

    String path(Object object);

    ProtectedResource guard(String path);
}
