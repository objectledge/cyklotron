package net.cyklotron.cms.rewrite;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.objectledge.web.rewrite.RewriteInfo;
import org.objectledge.web.rewrite.RewriteInfoBuilder;
import org.objectledge.web.rewrite.UrlRewriter;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.rewrite.UrlRewriteParticipant.RewriteTarget;

public class UrlRewriteRegistryImpl
    implements UrlRewriter, UrlRewriteRegistry
{
    private final List<UrlRewriteParticipant> participants;

    private AbstractSet<String> allPaths = new AllPaths();

    public UrlRewriteRegistryImpl(UrlRewriteParticipant[] participants)
    {
        super();
        this.participants = Arrays.asList(participants);
    }

    @Override
    public boolean matches(RewriteInfo request)
    {
        return allPaths.contains(request.getPathInfo());
    }

    @Override
    public Set<String> getPaths()
    {
        return allPaths;
    }

    @Override
    public void drop(String path)
    {
        for(UrlRewriteParticipant participant : participants)
        {
            participant.drop(path);
        }
    }

    @Override
    public Map<String, Map<String, String>> getRewriteInfo()
    {
        Map<String, Map<String, String>> info = new HashMap<>();
        for(UrlRewriteParticipant participant : participants)
        {
            Map<String, String> rewrites = new HashMap<>();
            Set<String> paths = participant.getPaths();
            for(String path : paths)
            {
                rewrites.put(path, formatRewrite(participant.rewrite(path)));
            }
            info.put(participant.getName(), rewrites);
        }
        return info;
    }

    @Override
    public String path(Object object)
    {
        for(UrlRewriteParticipant participant : participants)
        {
            String path = participant.path(object);
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
        for(UrlRewriteParticipant participant : participants)
        {
            if(participant.matches(request.getPathInfo()))
            {
                RewriteTarget target = participant.rewrite(request.getPathInfo());
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

    @Override
    public ProtectedResource guard(String path)
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
        extends AbstractSet<String>
    {
        @Override
        public boolean contains(Object o)
        {
            if(o instanceof String)
            {
                for(UrlRewriteParticipant participant : participants)
                {
                    if(participant.matches((String)o))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public Iterator<String> iterator()
        {
            final Iterator<UrlRewriteParticipant> i = participants.iterator();
            return new Iterator<String>()
                {
                    private Iterator<String> j = i.hasNext() ? i.next().getPaths().iterator()
                        : null;

                    @Override
                    public boolean hasNext()
                    {
                        return j != null && j.hasNext() || i.hasNext();
                    }

                    @Override
                    public String next()
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
