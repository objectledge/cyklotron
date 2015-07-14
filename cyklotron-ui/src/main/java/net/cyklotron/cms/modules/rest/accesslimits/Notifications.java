package net.cyklotron.cms.modules.rest.accesslimits;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.coral.web.rest.RequireCoralRole;

import net.cyklotron.cms.accesslimits.NotificationsConfigResource;
import net.cyklotron.cms.modules.rest.accesslimits.dto.NotificationConfigDTO;

@Path("/accesslimits/notifications")
@RequireCoralRole("cms.administrator")
public class Notifications
{
    private static final String CONFIG = "/cms/accesslimits/notifications";

    private final CoralSession coralSession;

    @Inject
    public Notifications(CoralSessionFactory coralSessionFactory)
    {
        coralSession = coralSessionFactory.getCurrentSession();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveConfiguration()
    {
        try
        {
            NotificationsConfigResource config = (NotificationsConfigResource)coralSession
                .getStore().getUniqueResourceByPath(CONFIG);
            NotificationConfigDTO response = new NotificationConfigDTO();
            response.setThreshold(config.getThreshold());
            response.setRecipient(config.getRecipient());
            response.setLocale(config.getLocale());
            response.setBaseURL(config.getBaseURL());
            return Response.ok(response).build();
        }
        catch(EntityDoesNotExistException | AmbigousEntityNameException e)
        {
            throw new InternalServerErrorException(e);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateConfiguration(NotificationConfigDTO request)
    {
        try
        {
            NotificationsConfigResource config = (NotificationsConfigResource)coralSession
                .getStore().getUniqueResourceByPath(CONFIG);
            config.setThreshold(request.getThreshold());
            config.setRecipient(request.getRecipient());
            config.setLocale(request.getLocale());
            config.setBaseURL(request.getBaseURL());
            return Response.noContent().build();
        }
        catch(EntityDoesNotExistException | AmbigousEntityNameException | ValueRequiredException e)
        {
            throw new InternalServerErrorException(e);
        }
    }

}
