package net.cyklotron.cms.organizations;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.site.SiteResource;

public class OrganizationRegistryCrossbar
    implements OrganizationRegistryService
{
    private final OrganizationRegistryService implementation;

    public OrganizationRegistryCrossbar(OrganizationRegistryService[] implementations,
        Configuration config)
        throws ConfigurationException
    {
        final Configuration implConfig = config.getChild("implementation");
        String className = implConfig.getValue().trim();
        OrganizationRegistryService selected = null;
        for(OrganizationRegistryService impl : implementations)
        {
            if(impl.getClass().getName().equals(className))
            {
                selected = impl;
            }
        }
        if(selected == null)
        {
            throw new ConfigurationException("implementation " + className + " is not available",
                implConfig.getPath(), implConfig.getLocation());
        }
        implementation = selected;
    }

    @Override
    public List<Organization> getOrganizations(String substring)
    {
        return implementation.getOrganizations(substring);
    }

    @Override
    public Organization getOrganization(long id)
        throws IOException
    {
        return implementation.getOrganization(id);
    }

    @Override
    public String getOrganizationNewsFeed(Parameters parameters)
        throws ProcessingException
    {
        return implementation.getOrganizationNewsFeed(parameters);
    }

    @Override
    public boolean checkOranizationId(Parameters parameters)
    {
        return implementation.checkOranizationId(parameters);
    }

    @Override
    public Collection<DocumentNodeResource> getOrganizationNewestDocuments(long organizationId,
        int maxAgeDays, Collection<SiteResource> sites, int limit, int offset)
        throws ProcessingException
    {
        return implementation.getOrganizationNewestDocuments(organizationId, maxAgeDays, sites,
            limit, offset);
    }
}
