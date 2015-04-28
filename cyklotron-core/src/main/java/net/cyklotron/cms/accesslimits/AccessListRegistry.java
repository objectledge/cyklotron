package net.cyklotron.cms.accesslimits;

import java.net.InetAddress;
import java.util.Set;

public interface AccessListRegistry
{
    /**
     * Searches active access lists and returns the names of the lists that contain address ranges
     * matching the given address.
     * 
     * @param address address to be checked.
     * @return names of all lists that contain address ranges matching the given address.
     */
    Set<String> getMatchingLists(InetAddress address);
}
