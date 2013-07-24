package net.cyklotron.cms.rewrite;

import java.util.Collection;
import java.util.Set;

import net.cyklotron.cms.ProtectedResource;

public interface UrlRewriteParticipant
{
    String getName();

    Set<SitePath> getPaths();

    Collection<RewriteEntry> getRewriteInfo();

    boolean matches(SitePath path);

    RewriteTarget rewrite(SitePath path);

    Collection<SitePath> potentialMatches(SitePath path);

    boolean canHandle(Object object);

    public void create(String path, Object object)
        throws UnsupportedClassException, PathInUseException;

    SitePath path(Object object)
        throws UnsupportedClassException;

    void drop(SitePath path);

    ProtectedResource guard(SitePath path);
}
