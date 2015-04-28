package net.cyklotron.cms.modules.rest.accesslimits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.coral.web.rest.RequireCoralRole;
import org.objectledge.utils.StackTrace;

import net.cyklotron.cms.accesslimits.AccessList;
import net.cyklotron.cms.accesslimits.AccessList.ValidationError;
import net.cyklotron.cms.accesslimits.AccessListResource;
import net.cyklotron.cms.accesslimits.AccessListResourceImpl;

@Path("/accesslimits/lists")
@RequireCoralRole("cms.administrator")
public class AccessLists
{
    private static final String LISTS_ROOT = "/cms/accesslimits/lists";

    private final CoralSession coralSession;

    private final UriInfo uriInfo;

    private static final Object LIST_NAME_LOCK = new Object();

    @Inject
    public AccessLists(CoralSessionFactory coralSessionFactory, UriInfo uriInfo)
    {
        this.uriInfo = uriInfo;
        this.coralSession = coralSessionFactory.getCurrentSession();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLists()
    {
        Resource[] lists = coralSession.getStore().getResourceByPath(LISTS_ROOT + "/*");
        return Response.ok(AccessListDto.create(lists, false)).build();
    }

    @GET
    @Path("/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getList(@PathParam("id") long id)
    {
        try
        {
            AccessListResource resource = (AccessListResource)coralSession.getStore().getResource(
                id);
            return Response.ok().entity(new AccessListDto(resource, true)).build();
        }
        catch(EntityDoesNotExistException | ClassCastException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createList(AccessListDto list)
    {
        List<AccessList.ValidationError> errors = AccessList.validate(list.getContents());
        if(errors.isEmpty())
        {
            synchronized(LIST_NAME_LOCK)
            {
                try
                {
                    Resource parent = coralSession.getStore().getUniqueResourceByPath(LISTS_ROOT);
                    Resource[] prev = coralSession.getStore().getResource(parent, list.getName());
                    if(prev.length == 0)
                    {
                        try
                        {
                            AccessListResource resource = AccessListResourceImpl
                                .createAccessListResource(coralSession, list.getName(), parent,
                                    list.getContents());
                            return Response
                                .created(uriInfo.getRequestUri().resolve(resource.getIdString()))
                                .header("X-Item-Id", resource.getIdString()).build();
                        }
                        catch(InvalidResourceNameException e)
                        {
                            return Response.status(Status.BAD_REQUEST)
                                .entity(new ErrorDto(ErrorDto.ErrorType.INVALID_NAME)).build();
                        }
                        catch(ValueRequiredException e)
                        {
                            return Response.status(Status.BAD_REQUEST)
                                .entity(new ErrorDto(ErrorDto.ErrorType.MISSING_CONTENTS)).build();
                        }
                    }
                    else
                    {
                        return Response.status(Status.CONFLICT).build();
                    }
                }
                catch(EntityDoesNotExistException | AmbigousEntityNameException e)
                {
                    return Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(new StackTrace(e).toString()).build();
                }
            }
        }
        else
        {
            return Response.status(Status.BAD_REQUEST)
                .entity(new ErrorDto(ErrorDto.ErrorType.INVALID_CONTENTS, errors)).build();
        }
    }

    @PUT
    @Path("/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateList(@PathParam("id") long id, AccessListDto list)
    {
        try
        {
            AccessListResource resource = (AccessListResource)coralSession.getStore().getResource(
                id);
            if(list.getContents() != null)
            {
                List<AccessList.ValidationError> errors = AccessList.validate(list.getContents());
                if(errors.isEmpty())
                {
                    if(!resource.getName().equals(list.getName()))
                    {
                        synchronized(LIST_NAME_LOCK)
                        {
                            Resource[] prev = coralSession.getStore().getResource(
                                resource.getParent(), list.getName());
                            if(prev.length == 0)
                            {
                                try
                                {
                                    coralSession.getStore().setName(resource, list.getName());
                                }
                                catch(InvalidResourceNameException e)
                                {
                                    return Response.status(Status.BAD_REQUEST)
                                        .entity(new ErrorDto(ErrorDto.ErrorType.INVALID_NAME))
                                        .build();
                                }
                            }
                            else
                            {
                                return Response.status(Status.CONFLICT).build();
                            }
                        }
                    }
                    try
                    {
                        resource.setContents(list.getContents());
                        return Response.status(Status.NO_CONTENT).build();
                    }
                    catch(ValueRequiredException e)
                    {
                        // shouldn't happen, we've checked list.getContents() for null above
                        return Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new StackTrace(e).toString()).build();
                    }
                }
                else
                {
                    return Response.status(Status.BAD_REQUEST)
                        .entity(new ErrorDto(ErrorDto.ErrorType.INVALID_CONTENTS, errors)).build();
                }
            }
            else
            {
                return Response.status(Status.BAD_REQUEST)
                    .entity(new ErrorDto(ErrorDto.ErrorType.MISSING_CONTENTS)).build();
            }
        }
        catch(EntityDoesNotExistException | ClassCastException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id:\\d+}")
    public Response deleteList(@PathParam("id") long id)
    {
        try
        {
            AccessListResource resource = (AccessListResource)coralSession.getStore().getResource(
                id);
            try
            {
                coralSession.getStore().deleteResource(resource);
                return Response.status(Status.NO_CONTENT).build();
            }
            catch(IllegalArgumentException | EntityInUseException e)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new StackTrace(e).toString()).build();
            }
        }
        catch(EntityDoesNotExistException | ClassCastException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    public static class AccessListDto
    {
        private Long id;

        private String name;

        private String contents;

        public AccessListDto()
        {
        }

        public AccessListDto(AccessListResource resource, boolean includeContents)
        {
            id = resource.getIdObject();
            name = resource.getName();
            if(includeContents)
            {
                contents = resource.getContents();
            }
        }

        public Long getId()
        {
            return id;
        }

        public void setId(Long id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getContents()
        {
            return contents;
        }

        public void setContents(String contents)
        {
            this.contents = contents;
        }

        public static List<AccessListDto> create(Resource[] items, boolean includeContents)
        {
            List<AccessListDto> result = new ArrayList<AccessListDto>(items.length);
            for(Resource item : items)
            {
                result.add(new AccessListDto((AccessListResource)item, includeContents));
            }
            Collections.sort(result, BY_NAME);
            return result;
        }

        private static final Comparator<AccessListDto> BY_NAME = new Comparator<AccessListDto>()
            {
                @Override
                public int compare(AccessListDto l1, AccessListDto l2)
                {
                    return l1.name.compareTo(l2.name);
                }
            };
    }

    public static class ErrorDto
    {
        public enum ErrorType
        {
            INVALID_NAME, MISSING_CONTENTS, INVALID_CONTENTS
        }

        private final ErrorType type;

        private final List<ValidationError> validationErrors;

        public ErrorDto(ErrorType type, List<ValidationError> validationErrors)
        {
            this.type = type;
            this.validationErrors = validationErrors;
        }

        public ErrorDto(ErrorType type)
        {
            this(type, null);
        }

        public ErrorType getType()
        {
            return type;
        }

        public List<ValidationError> getValidationErrors()
        {
            return validationErrors;
        }
    }
}
