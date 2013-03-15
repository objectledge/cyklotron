package net.cyklotron.cms.modules.rest.category.query;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.web.rest.RequireCoralRole;
import org.objectledge.coral.store.Resource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.site.SiteResource;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

@Path("/category/query")
public class CategoryQuery
{

    private final CoralSessionFactory coralSessionFactory;

    @Inject
    public CategoryQuery(CoralSessionFactory coralSessionFactory)
    {
        this.coralSessionFactory = coralSessionFactory;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequireCoralRole("cms.administrator")
    public Response getAllForSite(@QueryParam("siteId") Long siteId)
    {
        CoralSession session = coralSessionFactory.getCurrentSession();
        if(siteId != null)
        {
            SiteResource site;
            try
            {
                site = getSiteResource(siteId, session);
            }
            catch(EntityDoesNotExistException e)
            {
                return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
            final Resource[] resourceByPath = session.getStore().getResourceByPath(site,
                "/applications/category_query/query/*");
            final Collection<CategoryQueryResource> categoryQueries = castToCategoryQuery(resourceByPath);
            return Response.ok(CategoryQueryDto.toDtos(categoryQueries)).build();
        }
        else
        {
            return Response.status(400).entity("No siteId parameter").build();
        }
    }

    private Collection<CategoryQueryResource> castToCategoryQuery(final Resource[] resourceByPath)
    {
        return Collections2.transform(Arrays.asList(resourceByPath),
            new Function<Resource, CategoryQueryResource>()
                {

                    @Override
                    public CategoryQueryResource apply(Resource resource)
                    {
                        return CategoryQueryResource.class.cast(resource);
                    }
                });
    }

    private SiteResource getSiteResource(Long siteId, CoralSession session)
        throws EntityDoesNotExistException
    {
        final SiteResource site = session.getStore().getResource(siteId.longValue(),
            SiteResource.class);
        return site;
    }
}
