package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.util.Set;

/**
 * DocumentService is used to operate on CMS documents.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DocumentService.java,v 1.4 2007-11-18 21:23:07 rafal Exp $
 */
public interface NgoDatabaseService
{
    /**
     * @return <code>orgniazations</code>
     */
    public Organizations getOrganizations();

    /**
     * @return <code>list of orgniazations witch name contians</code>
     */
    public Set<Organization> getOrganizations(String substring);

    /**
     * @return <code>organization whit id is set as parameter</code>
     */
    public Organization getOrganization(Long id);

    /**
     * update ogranizations from metadata.
     */
    public void update();

    /**
     * Download medatata.
     */
    public void downloadSource()
        throws IOException;
}
