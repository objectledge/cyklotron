package net.cyklotron.cms.modules.rest.login;

import java.security.Principal;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.BlockedReason;
import org.objectledge.authentication.UserManager;

@Path("/login")
public class AccountStatus
{
    private final UserManager userManager;

    @Inject
    public AccountStatus(UserManager userManager)
    {
        this.userManager = userManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountStatus(@QueryParam("uid") String uid)
    {
        try
        {
            Principal account = userManager.getUserByLogin(uid);
            if(userManager.isUserPasswordExpired(account))
            {
                userManager.setUserShadowFlag(account, BlockedReason.PASSWORD_EXPIRED.getCode()
                    .toString());
            }
            BlockedReason blockedReason = userManager.checkAccountFlag(account);
            Long expiration = userManager.getUserPasswordExpirationDays(account);
            AccountStatusDto result = new AccountStatusDto(uid, blockedReason.getShortReason(),
                expiration);
            return Response.ok(result).build();
        }
        catch(AuthenticationException e)
        {
            AccountStatusDto result = new AccountStatusDto(uid, "invalid_credentials", 0L);
            return Response.ok(result).build();
        }
    }
}
