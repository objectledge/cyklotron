package net.cyklotron.cms.rewrite;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.web.rewrite.RewriteInfo;
import org.objectledge.web.rewrite.RewriteInfoBuilder;
import org.objectledge.web.rewrite.UrlRewriter;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

public class UrlRewriteRegistryImpl
    implements UrlRewriter, UrlRewriteRegistry
{
    private final List<UrlRewriteParticipant> participants;

    private Set<SitePath> allPaths = new AllPaths();

    private final SiteService siteService;

    private final CoralSessionFactory coralSessionFactory;

    public UrlRewriteRegistryImpl(UrlRewriteParticipant[] participants, SiteService siteService,
        CoralSessionFactory coralSessionFactory)
    {
        super();
        this.siteService = siteService;
        this.coralSessionFactory = coralSessionFactory;
        this.participants = Arrays.asList(participants);
    }

    @Override
    public boolean matches(RewriteInfo request)
    {
        final SitePath path = sitePath(request);
        final Map<SitePath, UrlRewriteParticipant> potentialMatches = potentialMatches(path);
        return !potentialMatches.isEmpty();
    }

    @Override
    public Set<SitePath> getPaths()
    {
        return allPaths;
    }

    @Override
    public boolean canHandle(Object object)
    {
        for(UrlRewriteParticipant participant : participants)
        {
            if(participant.canHandle(object))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void create(String path, Object object)
        throws UnsupportedClassException, PathInUseException
    {
        for(UrlRewriteParticipant participant : participants)
        {
            if(participant.canHandle(object))
            {
                participant.create(path, object);
                return;
            }
        }
        throw new UnsupportedClassException("can't handle " + object.getClass().getName());
    }

    @Override
    public void drop(SitePath path)
    {
        for(UrlRewriteParticipant participant : participants)
        {
            participant.drop(path);
        }
    }

    @Override
    public Collection<RewriteEntry> getRewriteInfo()
    {
        Collection<RewriteEntry> infos = new ArrayList<>();
        for(UrlRewriteParticipant participant : participants)
        {
            infos.addAll(participant.getRewriteInfo());
        }
        return infos;
    }

    @Override
    public SitePath path(Object object)
        throws UnsupportedClassException
    {
        for(UrlRewriteParticipant participant : participants)
        {
            if(participant.canHandle(object))
            {
                return participant.path(object);
            }
        }
        throw new UnsupportedClassException("can't handle " + object.getClass().getName());
    }

    @Override
    public RewriteInfo rewrite(RewriteInfo request)
    {
        final SitePath path = sitePath(request);
        final Map<SitePath, UrlRewriteParticipant> potentialMatches = potentialMatches(path);
        if(potentialMatches.size() > 0)
        {
            final SitePath match = longestMatch(potentialMatches.keySet());
            final UrlRewriteParticipant participant = potentialMatches.get(match);
            final RewriteTarget target = participant.rewrite(match);
            final String remainingPathInfo = request.getServletPath().length() > match.getPath()
                .length() ? request.getServletPath().substring(match.getPath().length()) : "";
            if(target != null)
            {
                RewriteInfoBuilder builder = RewriteInfoBuilder.fromRewriteInfo(request);

                builder.withServletPath("/ledge").withPathInfo(
                    "/x/" + target.getNode().getIdString() + remainingPathInfo);
                for(Map.Entry<String, List<String>> entry : target.getParameters().entrySet())
                {
                    builder.withFormParameter(entry.getKey(), entry.getValue());
                }
                return builder.build();
            }
        }
        return request;
    }

    private Map<SitePath, UrlRewriteParticipant> potentialMatches(SitePath path)
    {
        Map<SitePath, UrlRewriteParticipant> results = new HashMap<>();
        for(UrlRewriteParticipant participant : participants)
        {
            Collection<SitePath> matches = participant.potentialMatches(path);
            for(SitePath match : matches)
            {
                results.put(match, participant);
            }
        }
        return results;
    }

    private SitePath longestMatch(Set<SitePath> matches)
    {
        SitePath longest = null;
        for(SitePath match : matches)
        {
            if(longest == null || match.getPath().length() > longest.getPath().length())
            {
                longest = match;
            }
        }
        return longest;
    }

    private SitePath sitePath(RewriteInfo request)
    {
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            SiteResource site = siteService.getSiteByAlias(coralSession, request.getRequest()
                .getServerName());
            return new SitePath(site, request.getServletPath());
        }
        catch(Exception e)
        {
            throw new RuntimeException("unable to map requested host to CMS site", e);
        }
    }

    @Override
    public ProtectedResource guard(SitePath path)
    {
        for(UrlRewriteParticipant participant : participants)
        {
            if(participant.matches(path))
            {
                return participant.guard(path);
            }
        }
        return null;
    }

    private final class AllPaths
        extends AbstractSet<SitePath>
    {
        @Override
        public boolean contains(Object o)
        {
            if(o instanceof SitePath)
            {
                for(UrlRewriteParticipant participant : participants)
                {
                    if(participant.matches((SitePath)o))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public Iterator<SitePath> iterator()
        {
            final Iterator<UrlRewriteParticipant> i = participants.iterator();
            return new Iterator<SitePath>()
                {
                    private Iterator<SitePath> j = i.hasNext() ? i.next().getPaths().iterator()
                        : null;

                    @Override
                    public boolean hasNext()
                    {
                        return j != null && j.hasNext() || i.hasNext();
                    }

                    @Override
                    public SitePath next()
                    {
                        if(j != null)
                        {
                            if(j.hasNext())
                            {
                                return j.next();
                            }
                            else
                            {
                                if(i.hasNext())
                                {
                                    j = i.next().getPaths().iterator();
                                    return j.next();
                                }
                            }
                        }
                        throw new NoSuchElementException();
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
        }

        @Override
        public int size()
        {
            int size = 0;
            for(UrlRewriteParticipant participant : participants)
            {
                size += participant.getPaths().size();
            }
            return size;
        }
    }
}
