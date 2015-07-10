package net.cyklotron.cms.accesslimits;

import java.net.InetAddress;

import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.web.ratelimit.impl.AccessListRegistry;
import org.objectledge.web.ratelimit.impl.HitTable.Hit;
import org.objectledge.web.ratelimit.impl.ThresholdChecker;

public class ThresholdCheckerImpl
    implements ThresholdChecker
{
    private static final String CONFIG_RES_PATH = "/cms/accesslimits/notifications";

    private AccessListRegistry accessListRegistry;

    private NotificationsConfigResource config;

    public ThresholdCheckerImpl(AccessListRegistry accessListRegistry,
        CoralSessionFactory coralSessionFactory)
    {
        this.accessListRegistry = accessListRegistry;
        try(CoralSession coralSession = coralSessionFactory.getRootSession())
        {
            config = (NotificationsConfigResource)coralSession.getStore().getUniqueResourceByPath(
                CONFIG_RES_PATH);
        }
        catch(EntityDoesNotExistException | AmbigousEntityNameException e)
        {
            throw new ComponentInitializationError(e);
        }
    }

    @Override
    public boolean isThresholdExceeded(InetAddress address, Hit hit)
    {
        return false;
    }
}
