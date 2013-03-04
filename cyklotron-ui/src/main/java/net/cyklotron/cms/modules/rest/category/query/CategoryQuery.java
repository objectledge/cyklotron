package net.cyklotron.cms.modules.rest.category.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.query.QueryResults.Row;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.site.SiteResource;

import com.google.common.base.Predicate;
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
    public Response getAllForSite(@QueryParam("siteId") Long siteId)
    {
        CoralSession session = coralSessionFactory.getCurrentSession();
        final Collection<CategoryQueryResource> categoryQueries = getAllCategoryQueries(session);
        if(siteId != null)
        {
            SiteResource site;
            try
            {
                site = getSiteResource(siteId, session);
            }
            catch(EntityDoesNotExistException e)
            {
                return Response.status(Status.BAD_REQUEST).build();
            }
            final String siteName = site.getName();
            final Collection<CategoryQueryResource> filtered = Collections2.filter(categoryQueries,
                new Predicate<CategoryQueryResource>()
                    {
                        @Override
                        public boolean apply(@Nullable CategoryQueryResource categoryQuery)
                        {
                            if(!categoryQuery.isAcceptedSitesDefined())
                            {
                                return true;
                            }
                            else
                            {
                                final TreeSet<String> siteNames = new TreeSet<>(Arrays
                                    .asList(categoryQuery.getAcceptedSiteNames()));
                                return siteNames.contains(siteName);
                            }
                        }
                    });
            return Response.ok(CategoryQueryDto.toDtos(filtered)).build();
        }
        else
        {
            return Response.ok(CategoryQueryDto.toDtos(categoryQueries)).build();
        }
    }

    private SiteResource getSiteResource(Long siteId, CoralSession session)
        throws EntityDoesNotExistException
    {
        final SiteResource site = session.getStore().getResource(siteId.longValue(),
            SiteResource.class);
        return site;
    }

    private Collection<CategoryQueryResource> getAllCategoryQueries(CoralSession session)
    {
        QueryResults results;
        try
        {
            results = session.getQuery().executeQuery(
                "FIND RESOURCE FROM " + CategoryQueryResource.CLASS_NAME);
            final Iterator<Row> iterator = results.iterator();
            Collection<CategoryQueryResource> categoryQueries = new ArrayList<>();
            while(iterator.hasNext())
            {
                categoryQueries.add(CategoryQueryResource.class.cast(iterator.next().get()));
            }
            return categoryQueries;
        }
        catch(MalformedQueryException e)
        {
            throw new BackendException("unexpected error, malformed query", e);
        }
    }
}
