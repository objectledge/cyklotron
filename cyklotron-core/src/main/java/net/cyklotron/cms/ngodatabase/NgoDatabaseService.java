package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.util.List;

import org.jcontainer.dna.ConfigurationException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.site.SiteException;

import com.sun.syndication.io.FeedException;

/**
 * Integration between Cyklotron and bazy.ngo.pl
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.4 2007-11-18 21:23:07 rafal Exp $
 */
public interface NgoDatabaseService
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

    /**
     * Update incoming organizations data from source.
     */
    public void updateIncoming();

    /**
     * Update outgoing organizations data file.
     */
    public void updateOutgoing();

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
}
