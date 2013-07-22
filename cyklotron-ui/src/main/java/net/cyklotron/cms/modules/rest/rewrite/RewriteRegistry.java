package net.cyklotron.cms.modules.rest.rewrite;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import net.cyklotron.cms.rewrite.RewriteEntry;
import net.cyklotron.cms.rewrite.RewriteTarget;
import net.cyklotron.cms.rewrite.SitePath;
import net.cyklotron.cms.rewrite.UrlRewriteRegistry;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

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
    @Path("{site}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response info(@PathParam("site") final String siteName)
    {
        final Collection<RewriteEntry> info = registry.getRewriteInfo();
        final Collection<RewriteEntry> siteInfo = transform(
            filter(info, new Predicate<RewriteEntry>()
                {
                    @Override
                    public boolean apply(RewriteEntry input)
                    {
                        return input.getSite().equals(siteName);
                    }
                }), new Function<RewriteEntry, RewriteEntry>()
                {
                    @Override
                    public RewriteEntry apply(RewriteEntry input)
                    {
                        return new RewriteEntry(input.getProvider(), input.getSite(), input
                            .getPath().replace("/", "__"), input.getTarget(), input
                            .getDescription());
                    }
                });
        return Response.ok(siteInfo).build();
    }

    @GET
    @Path("{site}/{path}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response check(@PathParam("site") String siteName, @PathParam("path") String path)
        throws SiteException
    {
        final CoralSession coralSession = coralSessionFactory.getCurrentSession();
        final SiteResource site = siteService.getSite(coralSession, siteName);

        RewriteTarget target = registry.target(new SitePath(site, path.replace("__", "/")));
        Map<String, Object> resp = new HashMap<>();
        resp.put("defined", Boolean.valueOf(target != null));
        if(target != null)
        {
            resp.put("target", registry.toUrl(target));
        }
        return Response.ok(resp).build();
    }

    @DELETE
    @Path("{site}/{path}")
    public Response drop(@PathParam("site") String siteName, @PathParam("path") String path)
        throws SiteException
    {
        final CoralSession coralSession = coralSessionFactory.getCurrentSession();
        final SiteResource site = siteService.getSite(coralSession, siteName);
        final SitePath sitePath = new SitePath(site, path.replace("__", "/"));

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
}
