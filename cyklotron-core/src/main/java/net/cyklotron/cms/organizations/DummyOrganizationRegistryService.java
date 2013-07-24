package net.cyklotron.cms.organizations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteResource;

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
    public Collection<DocumentNodeResource> getOrganizationNewestDocuments(long organizationId,
        int maxAgeDays, Collection<SiteResource> sites, int limit, int offset)
        throws ProcessingException
    {
        return Collections.emptyList();
    }
}
