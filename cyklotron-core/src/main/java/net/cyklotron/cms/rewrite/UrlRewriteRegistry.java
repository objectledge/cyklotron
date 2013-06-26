package net.cyklotron.cms.rewrite;

import java.util.Collection;
import java.util.Set;

import net.cyklotron.cms.ProtectedResource;

public interface UrlRewriteRegistry
{
    Set<SitePath> getPaths();

    Collection<RewriteEntry> getRewriteInfo();

    boolean canHandle(Object object);

    public void create(String path, Object object)
        throws UnsupportedClassException, PathInUseException;

    SitePath path(Object object)
        throws UnsupportedClassException;

    void drop(SitePath path);

    ProtectedResource guard(SitePath path);
}
