package net.cyklotron.cms.rewrite;

import java.util.AbstractSet;
import java.util.Arrays;
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
        return allPaths.contains(sitePath(request));
    }

    @Override
    public Set<SitePath> getPaths()
    {
        return allPaths;
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
    public Map<String, Map<SitePath, String>> getRewriteInfo()
    {
        Map<String, Map<SitePath, String>> info = new HashMap<>();
        for(UrlRewriteParticipant participant : participants)
        {
            Map<SitePath, String> rewrites = new HashMap<>();
            Set<SitePath> paths = participant.getPaths();
            for(SitePath path : paths)
            {
                rewrites.put(path, formatRewrite(participant.rewrite(path)));
            }
            info.put(participant.getName(), rewrites);
        }
        return info;
    }

    @Override
    public SitePath path(Object object)
    {
        for(UrlRewriteParticipant participant : participants)
        {
            SitePath path = participant.path(object);
            if(path != null)
            {
                return path;
            }
        }
        return null;
    }

    private String formatRewrite(RewriteTarget rewrite)
    {
        StringBuilder b = new StringBuilder();
        b.append("/ledge/x/").append(rewrite.getNode().getIdString());
        Map<String, List<String>> params = rewrite.getParameters();
        if(params.size() > 0)
        {
            b.append('?');
            for(Map.Entry<String, List<String>> entry : params.entrySet())
            {
                for(String value : entry.getValue())
                {
                    b.append(entry.getKey()).append('=').append(value);
                }
            }
        }
        return b.toString();
    }

    @Override
    public RewriteInfo rewrite(RewriteInfo request)
    {
        final SitePath path = sitePath(request);
        for(UrlRewriteParticipant participant : participants)
        {
            if(participant.matches(path))
            {
                RewriteTarget target = participant.rewrite(path);
                if(target != null)
                {
                    RewriteInfoBuilder builder = RewriteInfoBuilder.fromRewriteInfo(request);

                    builder.withServletPath("/ledge").withPathInfo(
                        "/x/" + target.getNode().getIdString());
                    for(Map.Entry<String, List<String>> entry : target.getParameters().entrySet())
                    {
                        builder.withFormParameter(entry.getKey(), entry.getValue());
                    }
                    return builder.build();
                }
            }
        }
        return request;
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
