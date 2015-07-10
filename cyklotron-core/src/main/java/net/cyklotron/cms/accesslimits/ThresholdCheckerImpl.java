package net.cyklotron.cms.accesslimits;

import java.net.InetAddress;

import org.objectledge.web.ratelimit.impl.HitTable.Hit;
import org.objectledge.web.ratelimit.impl.ThresholdChecker;

public class ThresholdCheckerImpl
    implements ThresholdChecker
{
    @Override
    public boolean isThresholdExceeded(InetAddress address, Hit hit)
    {
        return false;
    }
}
