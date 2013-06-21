package net.cyklotron.cms.modules.rest.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.rewrite.SitePath;
import net.cyklotron.cms.rewrite.UrlRewriteRegistry;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

@Path("/rewriteRegistry")
public class RewriteRegistry
{
    @Inject
    private UrlRewriteRegistry registry;

    @Inject
    private CoralSessionFactory coralSessionFactory;

    @Inject
    private SiteService siteService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response info()
    {
        final Map<String, Map<SitePath, String>> info = registry.getRewriteInfo();
        Map<String, List<RewriteInfoItem>> info2 = Maps.transformValues(info,
            new Function<Map<SitePath, String>, List<RewriteInfoItem>>()
                {
                    @Override
                    public List<RewriteInfoItem> apply(Map<SitePath, String> input)
                    {
                        final ArrayList<RewriteInfoItem> list = new ArrayList<>(Collections2.transform(input.entrySet(),
                            new Function<Map.Entry<SitePath, String>, RewriteInfoItem>()
                                {
                                    @Override
                                    public RewriteInfoItem apply(Entry<SitePath, String> input)
                                    {
                                        SiteResource site = input.getKey().getSite();
                                        return new RewriteInfoItem(site.getName(), input.getKey()
                                            .getPath(), input.getValue());
                                    }
                                }));
                        Collections.sort(list);
                        return list;
                    }
                });
        return Response.ok(info2).build();
    }

    @GET
    @Path("{site}/{path}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response check(@PathParam("site") String siteName, @PathParam("path") String path)
        throws SiteException
    {
        final CoralSession coralSession = coralSessionFactory.getCurrentSession();
        final SiteResource site = siteService.getSite(coralSession, siteName);

        boolean defined = registry.getPaths().contains(new SitePath(site, "/" + path));
        return Response.ok(Collections.singletonMap("defined", defined)).build();
    }

    @DELETE
    @Path("{site}/{path}")
    public Response drop(@PathParam("site") String siteName, @PathParam("path") String path)
        throws SiteException
    {
        final CoralSession coralSession = coralSessionFactory.getCurrentSession();
        final SiteResource site = siteService.getSite(coralSession, siteName);
        final SitePath sitePath = new SitePath(site, "/" + path);

        if(registry.getPaths().contains(sitePath))
        {
            ProtectedResource guard = registry.guard(sitePath);
            if(coralSession == null
                || !guard.canModify(coralSession, coralSession.getUserSubject()))
            {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            registry.drop(sitePath);
            return Response.ok().build();
        }
        else
        {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    public static class RewriteInfoItem
        implements Comparable<RewriteInfoItem>
    {
        private final String site;

        private final String path;

        private final String target;

        public RewriteInfoItem(String site, String path, String target)
        {
            this.site = site;
            this.path = path;
            this.target = target;
        }

        public String getSite()
        {
            return site;
        }

        public String getPath()
        {
            return path;
        }

        public String getTarget()
        {
            return target;
        }

        @Override
        public int compareTo(RewriteInfoItem that)
        {
            int s = this.site.compareTo(that.site);
            return s == 0 ? this.path.compareTo(that.path) : s;
        }
    }
}
