package net.cyklotron.cms.modules.rest.canonicallinkrules;

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

import net.cyklotron.cms.canonical.LinkCanonicalRuleResource;
import net.cyklotron.cms.canonical.LinkCanonicalRuleResourceImpl;
import net.cyklotron.cms.category.CategoryResource;

import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.coral.web.rest.RequireCoralRole;

@Path("/canonicallinkrules/rules")
@RequireCoralRole("cms.administrator")
public class Rules
{
    private static final String CANONICAL_LINK_RULES_ROOT = "/cms/canonicalLinkRules";

    private final CoralSession coralSession;

    private final UriInfo uriInfo;

    private static final Object ACTION_NAME_LOCK = new Object();

    @Inject
    public Rules(CoralSessionFactory coralSessionFactory, UriInfo uriInfo)
    {
        this.uriInfo = uriInfo;
        this.coralSession = coralSessionFactory.getCurrentSession();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveActions()
    {
        Resource[] rules = coralSession.getStore().getResourceByPath(
            CANONICAL_LINK_RULES_ROOT + "/*");
        return Response.ok(LinkCanonicalRuleDto.create(rules)).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAction(@PathParam("id") long id)
    {
        try
        {
            LinkCanonicalRuleResource current = (LinkCanonicalRuleResource)LinkCanonicalRuleResourceImpl
                .getLinkCanonicalRuleResource(coralSession, id);
            return Response.ok(new LinkCanonicalRuleDto(current)).build();
        }
        catch(EntityDoesNotExistException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAction(LinkCanonicalRuleDto rule)
    {
        try
        {
            Resource parent = coralSession.getStore().getUniqueResourceByPath(
                CANONICAL_LINK_RULES_ROOT);
            synchronized(ACTION_NAME_LOCK)
            {
                LinkCanonicalRuleResource linkRuleRes = LinkCanonicalRuleResourceImpl
                    .createLinkCanonicalRuleResource(coralSession, rule.getName(), parent,
                        rule.getCategory(), rule.getLinkPattern());
                linkRuleRes.setPriority(rule.getPriority());
                linkRuleRes.update();
                return Response.created(uriInfo.getRequestUri().resolve(linkRuleRes.getIdString()))
                    .header("X-Rule-Id", linkRuleRes.getIdString()).build();
            }
        }
        catch(InvalidResourceNameException | EntityDoesNotExistException
                        | AmbigousEntityNameException | ValueRequiredException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getStackTrace()).build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAction(@PathParam("id") long id, LinkCanonicalRuleDto rule)
    {
        try
        {
            LinkCanonicalRuleResource current = LinkCanonicalRuleResourceImpl
                .getLinkCanonicalRuleResource(coralSession, id);
            current.setPriority(rule.getPriority());
            current.setCategory(rule.getCategory());
            current.setLinkPattern(rule.getLinkPattern());
            current.update();
            return Response.noContent().build();
        }
        catch(EntityDoesNotExistException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
        catch(ClassCastException | ValueRequiredException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getStackTrace()).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteAction(@PathParam("id") long id)
    {
        try
        {
            LinkCanonicalRuleResource current = LinkCanonicalRuleResourceImpl
                .getLinkCanonicalRuleResource(coralSession, id);
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
        catch(ClassCastException | IllegalArgumentException | EntityInUseException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getStackTrace()).build();
        }
    }

    public static class LinkCanonicalRuleDto
    {
        private long id;

        private String name;

        private CategoryResource category;

        private int priority;

        private String linkPattern;

        public LinkCanonicalRuleDto()
        {
        }

        public LinkCanonicalRuleDto(LinkCanonicalRuleResource link)
        {
            id = link.getId();
            name = link.getName();
            category = (CategoryResource)link.getCategory();
            linkPattern = link.getLinkPattern();
            priority = link.getPriority(0);
        }

        public long getId()
        {
            return id;
        }

        public void setId(long id)
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

        public CategoryResource getCategory()
        {
            return category;
        }

        public void setCategory(CategoryResource category)
        {
            this.category = category;
        }

        public String getLinkPattern()
        {
            return linkPattern;
        }

        public void setLinkPattern(String linkPattern)
        {
            this.linkPattern = linkPattern;
        }

        public int getPriority()
        {
            return priority;
        }

        public void setPriority(int priority)
        {
            this.priority = priority;
        }

        private static final Comparator<LinkCanonicalRuleDto> BY_PRIORITY = new Comparator<LinkCanonicalRuleDto>()
            {
                @Override
                public int compare(LinkCanonicalRuleDto o1, LinkCanonicalRuleDto o2)
                {
                    return o1.getPriority() - o2.getPriority();
                }
            };

        public static Collection<LinkCanonicalRuleDto> create(Resource[] linkCanonicals)
        {
            List<LinkCanonicalRuleDto> result = new ArrayList<LinkCanonicalRuleDto>(
                linkCanonicals.length);
            for(Resource linkCanonical : linkCanonicals)
            {
                result.add(new LinkCanonicalRuleDto((LinkCanonicalRuleResource)linkCanonical));
            }
            Collections.sort(result, BY_PRIORITY);
            return result;
        }
    }
}
