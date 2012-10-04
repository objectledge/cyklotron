package net.cyklotron.cms.organizations;

import java.util.Collections;
import java.util.List;

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
}
