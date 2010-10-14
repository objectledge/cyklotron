package net.cyklotron.cms.ngodatabase;

import java.util.Set;

/**
 * Integration between Cyklotron and bazy.ngo.pl
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.4 2007-11-18 21:23:07 rafal Exp $
 */
public interface NgoDatabaseService
{
    /**
     * Retrieve all organizations.
     * 
     * @return list of organizations.
     */
    public Organizations getOrganizations();

    /**
     * Retrieve organizations with matching names.
     * 
     * @param substring substring to be searched withing organization names.
     * @return list of organizations.
     */
    public Set<Organization> getOrganizations(String substring);

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
}
