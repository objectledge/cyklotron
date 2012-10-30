package net.cyklotron.cms.organizations;

import java.util.Collections;
import java.util.List;

import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

public class DummyOrganizationRegistryService
    implements OrganizationRegistryService
{

    @Override
    public List<Organization> getOrganizations(String substring)
    {
        return Collections.emptyList();
    }

    @Override
    public Organization getOrganization(long id)
    {
        return null;
    }

    @Override
    public String getOrganizationNewsFeed(Parameters parameters)
        throws ProcessingException
    {
        return "";
    }
}
