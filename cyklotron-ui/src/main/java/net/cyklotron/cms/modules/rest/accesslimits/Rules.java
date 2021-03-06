package net.cyklotron.cms.modules.rest.accesslimits;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
import org.objectledge.web.ratelimit.impl.RuleFactory;
import org.objectledge.web.ratelimit.rules.ParseException;

import net.cyklotron.cms.accesslimits.ProtectedItemResource;
import net.cyklotron.cms.accesslimits.ProtectedItemResourceImpl;
import net.cyklotron.cms.accesslimits.RuleResource;
import net.cyklotron.cms.accesslimits.RuleResourceImpl;
import net.cyklotron.cms.modules.rest.accesslimits.dto.ErrorDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.ProtectedItemDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.RuleDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.ValidationRequestDTO;
import net.cyklotron.cms.modules.rest.accesslimits.dto.ValidationResponseDTO;

@Path("/accesslimits/rules")
@RequireCoralRole("cms.administrator")
public class Rules
{
    private static final String RULES_ROOT = "/cms/accesslimits/rules";

    private final CoralSession coralSession;

    private final UriInfo uriInfo;

    private static final Object ITEM_NAME_LOCK = new Object();

    @Inject
    public Rules(CoralSessionFactory coralSessionFactory, UriInfo uriInfo)
    {
        this.uriInfo = uriInfo;
        this.coralSession = coralSessionFactory.getCurrentSession();
    }

    @GET
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveProtectedItems()
    {
        Resource[] actions = coralSession.getStore().getResourceByPath(RULES_ROOT + "/*");
        return Response.ok(ProtectedItemDTO.create(actions)).build();
    }

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProctectedItem(ProtectedItemDTO item)
    {
        try
        {
            validateProtectedItem(item);
        }
        catch(PatternSyntaxException | ParseException e)
        {
            return Response.status(Status.BAD_REQUEST).entity(new ErrorDTO(e)).build();
        }

        try
        {
            String name;
            ProtectedItemResource itemResource;
            synchronized(ITEM_NAME_LOCK)
            {
                Resource parent = coralSession.getStore().getUniqueResourceByPath(RULES_ROOT);
                name = nextName(parent.getChildren());
                itemResource = ProtectedItemResourceImpl.createProtectedItemResource(coralSession,
                    name, parent, item.getUrlPattern());
            }
            int n = 1;
            for(RuleDTO rule : item.getRules())
            {
                RuleResourceImpl.createRuleResource(coralSession, Integer.toString(n),
                    itemResource, n, rule.getRuleDefinition(), rule.getRuleName());
                n++;
            }
            return Response.created(uriInfo.getRequestUri().resolve(itemResource.getIdString()))
                .header("X-Item-Id", itemResource.getIdString()).build();
        }
        catch(EntityDoesNotExistException | AmbigousEntityNameException | ValueRequiredException
                        | InvalidResourceNameException e)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(new StackTrace(e).toString()).build();
        }
    }

    private String nextName(Resource[] children)
    {
        int max = 0;
        for(Resource child : children)
        {
            int n = Integer.parseInt(child.getName());
            max = n > max ? n : max;
        }
        return Integer.toString(max + 1);
    }

    @GET
    @Path("/items/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveProtectedItem(@PathParam("id") long id)
    {
        try
        {
            Resource res = coralSession.getStore().getResource(id);
            return Response.ok(new ProtectedItemDTO((ProtectedItemResource)res)).build();
        }
        catch(EntityDoesNotExistException | ClassCastException e)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/items/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProtectedItem(@PathParam("id") long id, ProtectedItemDTO item)
    {
        try
        {
            validateProtectedItem(item);
        }
        catch(PatternSyntaxException | ParseException e)
        {
            return Response.status(Status.BAD_REQUEST).entity(new ErrorDTO(e)).build();
        }

        try
        {
            ProtectedItemResource res = (ProtectedItemResource)coralSession.getStore().getResource(
                id);
            try
            {
                res.setUrlPattern(item.getUrlPattern());
                res.update();
                Resource[] cur = res.getChildren();
                if(item.getRules() != null && item.getRules().size() > 0)
                {
                    int p = 1;
                    for(RuleDTO rule : item.getRules())
                    {
                        rule.setPriority(p++);
                    }
                    int n = cur.length + 1;
                    for(RuleDTO rule : item.getRules())
                    {
                        RuleResource curRule = rule.getId() != null ? getRuleResource(cur,
                            rule.getId()) : null;
                        if(curRule == null)
                        {
                            RuleResourceImpl.createRuleResource(coralSession,
                                Integer.toString(n++), res, rule.getPriority(),
                                rule.getRuleDefinition(), rule.getRuleName());
                        }
                        else
                        {
                            curRule.setPriority(rule.getPriority());
                            curRule.setRuleDefinition(rule.getRuleDefinition());
                            curRule.update();
                        }
                    }
                    for(Resource curRule : cur)
                    {
                        if(getRuleDao(item.getRules(), curRule.getId()) == null)
                        {
                            coralSession.getStore().deleteResource(curRule);
                        }
                    }
                }
                else
                {
                    // no rules array in item - delete all rules from db
                    for(Resource curRule : cur)
                    {
                        coralSession.getStore().deleteResource(curRule);
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

    private RuleResource getRuleResource(Resource[] rs, long id)
    {
        for(Resource r : rs)
        {
            if(r.getId() == id)
            {
                return (RuleResource)r;
            }
        }
        return null;
    }

    private RuleDTO getRuleDao(List<RuleDTO> rules, long id)
    {
        for(RuleDTO rule : rules)
        {
            if(rule.getId() != null && rule.getId() == id)
            {
                return rule;
            }
        }
        return null;
    }

    @DELETE
    @Path("/items/{id}")
    public Response deleteProtectedItem(@PathParam("id") long id)
    {
        try
        {
            Resource res = coralSession.getStore().getResource(id);
            try
            {
                synchronized(ITEM_NAME_LOCK)
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
    @Path("/validate/urlPattern")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateUrlPattern(ValidationRequestDTO request)
    {
        try
        {
            validateUrlPattern(request.getText());
            return Response.ok(new ValidationResponseDTO(true)).build();
        }
        catch(PatternSyntaxException e)
        {
            return Response.ok(new ValidationResponseDTO(false, e.getMessage())).build();
        }
    }

    @POST
    @Path("/validate/rule")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateRuleDefinition(ValidationRequestDTO request)
    {
        try
        {
            validateRuleDefinition(request.getText());
            return Response.ok(new ValidationResponseDTO(true)).build();
        }
        catch(ParseException e)
        {
            return Response.ok(new ValidationResponseDTO(false, e.getMessage())).build();
        }
    }

    private void validateUrlPattern(String urlPattern)
        throws PatternSyntaxException
    {
        Pattern.compile(urlPattern);
    }

    private void validateRuleDefinition(String rule)
        throws ParseException
    {
        RuleFactory.getInstance().validateRule(rule);
    }

    private void validateProtectedItem(ProtectedItemDTO item)
        throws PatternSyntaxException, ParseException
    {
        validateUrlPattern(item.getUrlPattern());
        if(item.getRules() != null)
        {
            for(RuleDTO rule : item.getRules())
            {
                validateRuleDefinition(rule.getRuleDefinition());
            }
        }
    }
}
