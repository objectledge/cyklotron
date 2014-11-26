package net.cyklotron.cms.modules.rest.accesslimits;

import java.util.ArrayList;
import java.util.Collection;
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
import org.objectledge.coral.web.rest.RequireCoralRole;
import org.objectledge.utils.StackTrace;

import net.cyklotron.cms.accesslimits.ActionResource;
import net.cyklotron.cms.accesslimits.ActionResourceImpl;

@Path("/accesslimits/actions")
@RequireCoralRole("cms.administrator")
public class Actions
{
    private static final String ACTIONS_ROOT = "/cms/accesslimits/actions";

    private final CoralSession coralSession;

    private final UriInfo uriInfo;

    private static final Object ACTION_NAME_LOCK = new Object();

    @Inject
    public Actions(CoralSessionFactory coralSessionFactory, UriInfo uriInfo)
    {
        this.uriInfo = uriInfo;
        this.coralSession = coralSessionFactory.getCurrentSession();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveActions()
    {
        Resource[] actions = coralSession.getStore().getResourceByPath(ACTIONS_ROOT + "/*");
        return Response.ok(ActionDto.create(actions)).build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAction(@PathParam("name") String name)
    {
        try
        {
            ActionResource current = (ActionResource)coralSession.getStore()
                .getUniqueResourceByPath(ACTIONS_ROOT + "/" + name);
            return Response.ok(new ActionDto(current)).build();
        }
        catch(EntityDoesNotExistException | AmbigousEntityNameException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAction(ActionDto action)
    {
        try
        {
            Resource parent = coralSession.getStore().getUniqueResourceByPath(ACTIONS_ROOT);
            synchronized(ACTION_NAME_LOCK)
            {
                if(!actionExists(action.getName(), parent.getChildren()))
                {
                    ActionResource res = ActionResourceImpl.createActionResource(coralSession,
                        action.getName(), parent);
                    res.setViewOverride(action.getViewOverride());
                    res.setParamsOverride(action.getParamsOverride());
                    res.update();
                    return Response.created(uriInfo.getRequestUri().resolve(action.getName()))
                        .build();
                }
                else
                {
                    return Response.status(Status.CONFLICT).build();
                }
            }
        }
        catch(InvalidResourceNameException | EntityDoesNotExistException
                        | AmbigousEntityNameException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new StackTrace(e).toString()).build();
        }
    }

    private boolean actionExists(String name, Resource[] children)
    {
        for(Resource child : children)
        {
            if(child.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    @PUT
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAction(@PathParam("name") String name, ActionDto action)
    {
        try
        {
            ActionResource current = (ActionResource)coralSession.getStore()
                .getUniqueResourceByPath(ACTIONS_ROOT + "/" + name);
            current.setViewOverride(action.getViewOverride());
            current.setParamsOverride(action.getParamsOverride());
            current.update();
            return Response.noContent().build();
        }
        catch(EntityDoesNotExistException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
        catch(ClassCastException | AmbigousEntityNameException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new StackTrace(e).toString()).build();
        }
    }

    @DELETE
    @Path("{name}")
    public Response deleteAction(@PathParam("name") String name)
    {
        try
        {
            ActionResource current = (ActionResource)coralSession.getStore()
                .getUniqueResourceByPath(ACTIONS_ROOT + "/" + name);
            synchronized(ACTION_NAME_LOCK)
            {
                coralSession.getStore().deleteResource(current);
            }
            return Response.noContent().build();
        }
        catch(EntityDoesNotExistException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
        catch(ClassCastException | AmbigousEntityNameException | IllegalArgumentException
                        | EntityInUseException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new StackTrace(e).toString()).build();
        }
    }

    public static class ActionDto
    {
        private String name;

        private String viewOverride;

        private String paramsOverride;

        public ActionDto()
        {
        }

        public ActionDto(ActionResource action)
        {
            name = action.getName();
            viewOverride = action.getViewOverride();
            paramsOverride = action.getParamsOverride();
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getViewOverride()
        {
            return viewOverride;
        }

        public void setViewOverride(String viewOverride)
        {
            this.viewOverride = viewOverride;
        }

        public String getParamsOverride()
        {
            return paramsOverride;
        }

        public void setParamsOverride(String paramsOverride)
        {
            this.paramsOverride = paramsOverride;
        }

        private static final Comparator<ActionDto> BY_NAME = new Comparator<ActionDto>()
            {
                @Override
                public int compare(ActionDto o1, ActionDto o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }
            };

        public static Collection<ActionDto> create(Resource[] actions)
        {
            List<ActionDto> result = new ArrayList<ActionDto>(actions.length);
            for(Resource action : actions)
            {
                result.add(new ActionDto((ActionResource)action));
            }
            Collections.sort(result, BY_NAME);
            return result;
        }
    }
}
