package net.cyklotron.cms.modules.rest.accesslimits;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import org.objectledge.net.CIDRBlock;
import org.objectledge.net.IPAddressUtil;
import org.objectledge.utils.StackTrace;

import net.cyklotron.cms.accesslimits.AccessListItemResource;
import net.cyklotron.cms.accesslimits.AccessListItemResourceImpl;
import net.cyklotron.cms.accesslimits.AccessListResource;
import net.cyklotron.cms.accesslimits.AccessListResourceImpl;
import net.cyklotron.cms.modules.rest.accesslimits.dto.AccessListDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.AccessListItemDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.AccessListSubmissionDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.ErrorDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.ValidationRequestDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.ValidationResponseDTO;

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
    public Response retrieveAccessLists()
    {
        Resource[] lists = coralSession.getStore().getResourceByPath(LISTS_ROOT + "/*");
        return Response.ok(AccessListDTO.create(lists)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAccessList(@PathParam("id") long id,
        @QueryParam("itemOrder") @DefaultValue("address") String itemOrder)
    {
        try
        {
            Resource res = coralSession.getStore().getResource(id);
            final AccessListDTO list = new AccessListDTO((AccessListResource)res, true);
            if("address".equals(itemOrder))
            {
                Collections.sort(list.getItems(), AccessListItemDTO.BY_ADDRESS_BLOCK);
            }
            else
            {
                Collections.sort(list.getItems(), AccessListItemDTO.BY_CREATION_TIME);
            }
            return Response.ok(list).build();
        }
        catch(EntityDoesNotExistException | ClassCastException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
        catch(UnknownHostException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new StackTrace(e).toString()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response creatAccessList(AccessListDTO list)
    {
        try
        {
            validateAccessList(list);
        }
        catch(UnknownHostException e)
        {
            return Response.status(Status.BAD_REQUEST).entity(new ErrorDTO(e)).build();
        }

        try
        {
            AccessListResource listResource;
            synchronized(LIST_NAME_LOCK)
            {
                Resource parent = coralSession.getStore().getUniqueResourceByPath(LISTS_ROOT);
                if(coralSession.getStore().getResource(parent, list.getName()).length > 0)
                {
                    return Response.status(Status.CONFLICT).build();
                }                
                listResource = AccessListResourceImpl.createAccessListResource(coralSession,
                    list.getName(), parent);
                listResource.setDescription(list.getDescription());
                listResource.update();
            }
            int n = 1;
            for(AccessListItemDTO item : list.getItems())
            {
                AccessListItemResource itemResource = AccessListItemResourceImpl
                    .createAccessListItemResource(coralSession, Integer.toString(n), listResource,
                        item.getAddressBlock());
                itemResource.setDescription(item.getDescription());
                itemResource.update();
                n++;
            }
            return Response.created(uriInfo.getRequestUri().resolve(listResource.getIdString()))
                .header("X-Item-Id", listResource.getIdString()).build();
        }
        catch(EntityDoesNotExistException | AmbigousEntityNameException | ValueRequiredException
                        | InvalidResourceNameException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new StackTrace(e).toString()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAccessList(@PathParam("id") long id, AccessListDTO list)
    {
        try
        {
            validateAccessList(list);
        }
        catch(UnknownHostException e)
        {
            return Response.status(Status.BAD_REQUEST).entity(new ErrorDTO(e)).build();
        }

        try
        {
            AccessListResource res = (AccessListResource)coralSession.getStore().getResource(id);
            try
            {
                if(!list.getName().equals(res.getName()))
                {
                    synchronized(LIST_NAME_LOCK)
                    {
                        if(coralSession.getStore().getResource(res.getParent(), list.getName()).length == 0)
                        {
                            coralSession.getStore().setName(res, list.getName());
                        }
                        else
                        {
                            return Response.status(Status.CONFLICT).build();
                        }
                    }
                }
                res.setDescription(list.getDescription());
                res.update();

                Resource[] cur = res.getChildren();
                if(list.getItems() != null && list.getItems().size() > 0)
                {
                    int n = cur.length + 1;
                    for(AccessListItemDTO item : list.getItems())
                    {
                        AccessListItemResource curItemResource = item.getId() != null ? getAccessListItemResource(
                            cur, item.getId()) : null;
                        if(curItemResource == null)
                        {
                            AccessListItemResource itemResource = AccessListItemResourceImpl
                                .createAccessListItemResource(coralSession, Integer.toString(n++),
                                    res, item.getAddressBlock());
                            itemResource.setDescription(item.getDescription());
                            itemResource.update();
                        }
                        else
                        {
                            curItemResource.setAddressBlock(item.getAddressBlock());
                            curItemResource.setDescription(item.getDescription());
                            curItemResource.update();
                        }
                    }
                    for(Resource curItemResource : cur)
                    {
                        if(getAccessListItemDTO(list.getItems(), curItemResource.getId()) == null)
                        {
                            coralSession.getStore().deleteResource(curItemResource);
                        }
                    }
                }
                else
                {
                    // no items array in list - delete all items from db
                    for(Resource curItemResource : cur)
                    {
                        coralSession.getStore().deleteResource(curItemResource);
                    }
                }
                return Response.noContent().build();
            }
            catch(Exception e)
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

    @DELETE
    @Path("/{id}")
    public Response deleteAccessLisst(@PathParam("id") long id)
    {
        try
        {
            Resource res = coralSession.getStore().getResource(id);
            try
            {
                synchronized(LIST_NAME_LOCK)
                {
                    coralSession.getStore().deleteTree(res);
                }
                return Response.noContent().build();
            }
            catch(EntityInUseException e)
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

    @POST
    @Path("/validate/listName")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateListName(ValidationRequestDTO request)
    {
        synchronized(LIST_NAME_LOCK)
        {
            if(coralSession.getStore().getResourceByPath(LISTS_ROOT + "/" + request.getText()).length == 0)
            {
                return Response.ok(new ValidationResponseDTO(true)).build();
            }
            else
            {
                return Response.ok(
                    new ValidationResponseDTO(false, "access list " + request.getText()
                        + " already exists")).build();
            }
        }
    }

    @POST
    @Path("/validate/addressBlock")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateAddressBlock(ValidationRequestDTO request)
    {
        try
        {
            validateAddressBlock(request.getText());
            return Response.ok(new ValidationResponseDTO(true)).build();
        }
        catch(UnknownHostException | IllegalArgumentException e)
        {
            return Response.ok(new ValidationResponseDTO(false, e.getMessage())).build();
        }
    }

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitListItem(AccessListSubmissionDTO request)
    {
        try
        {
            InetAddress addr = IPAddressUtil.byAddress(request.getAddress());
            byte[] addrBytes = addr.getAddress();
            switch(request.getRange())
            {
            case 8:
                addrBytes[1] = 0;
            case 16:
                addrBytes[2] = 0;
            case 24:
                addrBytes[3] = 0;
            }
            addr = InetAddress.getByAddress(addrBytes);
            CIDRBlock cidr = new CIDRBlock(addr, request.getRange());
            
            AccessListResource res = (AccessListResource)coralSession.getStore().getResource(
                request.getListId());

            AccessListItemResource itemResource = AccessListItemResourceImpl
                .createAccessListItemResource(coralSession, Integer.toString(res.getChildren().length), res, cidr.toString());
            itemResource.setDescription(request.getDescription());
            itemResource.update();
            return Response.noContent().build();
        }
        catch(UnknownHostException | IllegalArgumentException | EntityDoesNotExistException
                        | ValueRequiredException | InvalidResourceNameException e)
        {
            throw new InternalServerErrorException(e);
        }
    }
    
    private void validateAddressBlock(String text)
        throws UnknownHostException
    {
        net.cyklotron.cms.accesslimits.AccessList.parse(text);
    }

    private void validateAccessList(AccessListDTO list)
        throws UnknownHostException
    {
        for(AccessListItemDTO item : list.getItems())
        {
            validateAddressBlock(item.getAddressBlock());
        }
    }

    private AccessListItemResource getAccessListItemResource(Resource[] rs, long id)
    {
        for(Resource r : rs)
        {
            if(r.getId() == id)
            {
                return (AccessListItemResource)r;
            }
        }
        return null;
    }

    private AccessListItemDTO getAccessListItemDTO(List<AccessListItemDTO> items, long id)
    {
        for(AccessListItemDTO rule : items)
        {
            if(rule.getId() != null && rule.getId() == id)
            {
                return rule;
            }
        }
        return null;
    }
}
