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

import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.coral.web.rest.RequireCoralRole;

import net.cyklotron.cms.canonical.LinkCanonicalRuleResource;
import net.cyklotron.cms.canonical.LinkCanonicalRuleResourceImpl;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.modules.rest.category.CategoryDto;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

@Path("/canonicallinkrules/rules")
@RequireCoralRole("cms.administrator")
public class Rules
{
    private static final String CANONICAL_LINK_RULES_ROOT = "/cms/canonicalLinkRules";

    private final CoralSession coralSession;

    private final UriInfo uriInfo;

    private static final Object ACTION_NAME_LOCK = new Object();

    private SiteService siteService;

    @Inject
    public Rules(CoralSessionFactory coralSessionFactory, SiteService siteService, UriInfo uriInfo)
    {
        this.siteService = siteService;
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
                Status errorStatus = ruleExists(rule, parent.getChildren());
                if(Status.OK.equals(errorStatus))
                {
                    CategoryResource category = CategoryResourceImpl.getCategoryResource(
                        coralSession, Long.parseLong(rule.category.getId()));
                    LinkCanonicalRuleResource linkRuleRes = LinkCanonicalRuleResourceImpl
                        .createLinkCanonicalRuleResource(coralSession, rule.getName(), parent,
                            category, rule.getLinkPattern());
                    linkRuleRes.setPriority(rule.getPriority());
                    if(rule.getSite() != null)
                    {
                        SiteResource site = siteService.getSite(coralSession, rule.getSite());
                        linkRuleRes.setSite(site);
                    }
                    linkRuleRes.update();
                    return Response
                        .created(uriInfo.getRequestUri().resolve(linkRuleRes.getIdString()))
                        .header("X-Rule-Id", linkRuleRes.getIdString()).build();
                }
                return Response.status(errorStatus).build();
            }
        }
        catch(InvalidResourceNameException | EntityDoesNotExistException
                        | AmbigousEntityNameException | ValueRequiredException | SiteException e)
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
            Resource parent = coralSession.getStore().getUniqueResourceByPath(
                CANONICAL_LINK_RULES_ROOT);

            synchronized(ACTION_NAME_LOCK)
            {
                Status errorStatus = ruleExists(rule, parent.getChildren());
                if(Status.OK.equals(errorStatus))
                {
                    LinkCanonicalRuleResource current = LinkCanonicalRuleResourceImpl
                        .getLinkCanonicalRuleResource(coralSession, id);
                    current.setPriority(rule.getPriority());
                    CategoryResource category = CategoryResourceImpl.getCategoryResource(
                        coralSession, Long.parseLong(rule.category.getId()));
                    current.setCategory(category);
                    current.setLinkPattern(rule.getLinkPattern());
                    if(rule.getSite() != null && rule.getSite().length() > 0)
                    {
                        SiteResource site = siteService.getSite(coralSession, rule.getSite());
                        current.setSite(site);
                    }
                    else
                    {
                        current.setSite(null);
                    }
                    current.update();
                    return Response.noContent().build();
                }
                return Response.status(errorStatus).build();
            }
        }
        catch(EntityDoesNotExistException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
        catch(AmbigousEntityNameException | ValueRequiredException | SiteException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getStackTrace()).build();
        }
    }

    /***
     * check if rule exist
     * 
     * @param rule
     * @param children
     * @param error type
     * @return boolean
     */
    private Status ruleExists(LinkCanonicalRuleDto rule, Resource[] children)
    {
        if(rule != null)
        {
            for(Resource child : children)
            {
                if(child instanceof LinkCanonicalRuleResource)
                {
                    LinkCanonicalRuleResource ruleResource = (LinkCanonicalRuleResource)child;
                    if(ruleResource.getId() != rule.getId())
                    {
                        if(ruleResource.getName().equals(rule.getName()))
                        {
                            return Status.CONFLICT;
                        }
                        if(ruleResource.getCategory().getIdString().equals(rule.category.getId()))
                        {
                            return Status.PRECONDITION_FAILED;
                        }
                    }
                }
            }
        }
        return Status.OK;
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

        private CategoryDto category;

        private String site;

        private int priority;

        private String linkPattern;

        public LinkCanonicalRuleDto()
        {
        }

        public LinkCanonicalRuleDto(LinkCanonicalRuleResource link)
        {
            id = link.getId();
            name = link.getName();
            category = CategoryDto.create((CategoryResource)link.getCategory());
            if(link.isSiteDefined())
            {
                site = link.getSite().getName();
            }
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

        public CategoryDto getCategory()
        {
            return category;
        }

        public void setCategory(CategoryDto category)
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

        public String getSite()
        {
            return site;
        }

        public void setSite(String site)
        {
            this.site = site;
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
