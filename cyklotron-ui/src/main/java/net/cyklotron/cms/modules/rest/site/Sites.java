package net.cyklotron.cms.modules.rest.site;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.web.rest.RequireAny;
import org.objectledge.coral.web.rest.RequireCoralRole;

import net.cyklotron.cms.site.SiteService;

@Path("sites")
public class Sites
{
    @Inject
    private CoralSessionFactory coralSessionFactory;

    @Inject
    private SiteService siteService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequireAny(roles = { @RequireCoralRole("cms.registered"),
                    @RequireCoralRole("cms.administrator") })
    public List<SiteDTO> retrieveSites()
    {
        try(CoralSession coralSession = coralSessionFactory.getAnonymousSession())
        {
            return SiteDTO.create(siteService.getSites(coralSession));
        }
    }
}
