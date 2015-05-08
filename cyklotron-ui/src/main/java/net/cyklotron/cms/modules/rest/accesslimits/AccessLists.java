package net.cyklotron.cms.modules.rest.accesslimits;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.web.rest.RequireCoralRole;

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

}
