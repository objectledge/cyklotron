package net.cyklotron.cms.modules.rest.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.category.CategoryResource;

@Path("/category")
public class Category
{
    private final CoralSessionFactory coralSessionFactory;

    @Inject
    public Category(CoralSessionFactory coralSessionFactory)
    {
        this.coralSessionFactory = coralSessionFactory;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories(@DefaultValue("All") @QueryParam("id") final List<String> ids)
        throws EntityDoesNotExistException
    {
        Collection<CategoryResource> categories = null;
        if(ids != null && ids.size() == 1 && ids.get(0).equals("All"))
        {
            // TODO current use case does not need all categories, implement it when needed.
            return Response.status(HttpServletResponse.SC_NOT_IMPLEMENTED).build();
        }
        else
        {
            categories = getOnlyWithIdentifiers(ids);
        }
        final Collection<CategoryDto> categoriesDtos = CategoryDto.create(categories);
        return Response.ok(categoriesDtos).build();
    }

    private Collection<CategoryResource> getOnlyWithIdentifiers(List<String> ids)
        throws EntityDoesNotExistException
    {
        Collection<CategoryResource> categories = new ArrayList<>(ids.size());
        CoralSession session = coralSessionFactory.getCurrentSession();
        for(String id : ids)
        {
            categories
                .add(session.getStore().getResource(Long.valueOf(id), CategoryResource.class));
        }
        return categories;
    }
}
