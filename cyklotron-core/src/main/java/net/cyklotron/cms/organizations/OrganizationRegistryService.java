package net.cyklotron.cms.organizations;

import java.util.List;

public interface OrganizationRegistryService
{
    /**
     * Retrieve organizations with matching names.
     * 
     * @param substring substring to be searched within organization names.
     * @return list of organizations.
     */
    public List<Organization> getOrganizations(String substring);

    /**
     * Retrieve organization data.
     * 
     * @return organization with specified id.
     */
    public Organization getOrganization(long id);
}
