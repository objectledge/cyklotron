package net.cyklotron.cms.organizations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.DocumentNodeResource;

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

    public boolean checkOranizationId(Parameters parameters)
    {
        return true;
    }

    @Override
    public Collection<DocumentNodeResource> getOrganizationNewestDocuments(Parameters parameters, int limit, int offset)
        throws ProcessingException
    {
        return Collections.emptyList();
    }
}
