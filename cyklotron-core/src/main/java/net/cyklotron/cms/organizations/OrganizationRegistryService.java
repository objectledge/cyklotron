package net.cyklotron.cms.organizations;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.DocumentNodeResource;

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
     * @throws IOException
     */
    public Organization getOrganization(long id)
        throws IOException;

    /**
     * Returns the contents of RSS/Atom news feed for an organization.
     * <P>
     * Content-Type header for the response should be text/xml and encoding should be UTF-8.
     * </p>
     * 
     * @param parameters request parameters, containing organization id
     * @return contents of the feed.
     */
    public String getOrganizationNewsFeed(Parameters parameters)
        throws ProcessingException;

    /**
     * Check if the request parameters contain organization a valid organization id parameter.
     * 
     * @param parameters
     * @return true if the parameter is present, is a number and points to an existing organization.
     */
    public boolean checkOranizationId(Parameters parameters);

    /**
     * Returns newest documents related to the organization.
     * 
     * @param parameters request parameters.
     * @param limit size of the paging window.
     * @param offset offset of the paging window.
     * @return a collection of 0 .. limit documents
     * @throws ProcessingException
     */
    Collection<DocumentNodeResource> getOrganizationNewestDocuments(long organizationId, int limit,
        int offset)
        throws ProcessingException;
}
